package com.acoldbottle.stockmate.api.sse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final EmitterRegistry emitterRegistry;
    private final SubscriberRegistry subscriberRegistry;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SseEmitter connect(Long userId) {
        Optional<SseEmitter> isExistEmitter = emitterRegistry.findByUserId(userId);
        if (isExistEmitter.isPresent()) {
            return isExistEmitter.get();
        }
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        emitterRegistry.save(userId, newEmitter);
        subscriberRegistry.saveAllByUserId(userId);
        log.info("[SseService] user={} --> login", userId);
        newEmitter.onCompletion(() -> {
            emitterRegistry.delete(userId);
            log.info("[SseService] user={} --> connection closed", userId);
        });
        newEmitter.onError((e) -> {
            emitterRegistry.delete(userId);
            log.error("[SseService] Connection Error! userId={}", userId, e);
        });
        return newEmitter;
    }

    public void disconnect(Long userId) {
        log.info("[SseService] user={} --> logout", userId);
        subscriberRegistry.delete(userId);
        SseEmitter removedEmitter = emitterRegistry.delete(userId);
        if (removedEmitter != null) {
            removedEmitter.complete();
        }
    }

    @PostConstruct
    public void sendPing() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Long, SseEmitter> entry : emitterRegistry.findAllEntry()) {
                Long userId = entry.getKey();
                SseEmitter emitter = entry.getValue();
                try {
                    emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
                } catch (IOException e) {
                    log.error("[SseService] userId={}",userId, e);
                    emitter.completeWithError(e);
                    emitterRegistry.delete(userId);
                    subscriberRegistry.delete(userId);
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        for (SseEmitter emitter : emitterRegistry.findAll()) {
            emitter.complete();
        }
        scheduler.shutdown();
        emitterRegistry.deleteAll();
        subscriberRegistry.deleteAll();
    }
}
