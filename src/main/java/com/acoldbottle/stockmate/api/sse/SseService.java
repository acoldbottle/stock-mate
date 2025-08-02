package com.acoldbottle.stockmate.api.sse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class SseService {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterMap.put(userId, emitter);
        emitter.onCompletion(() -> emitterMap.remove(userId));
        emitter.onError((e) -> {
            emitterMap.remove(userId);
            log.error("[SseService] Connection Error! userId={}", userId, e);
        });
        return emitter;
    }

    @PostConstruct
    public void sendPing() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Long, SseEmitter> entry : emitterMap.entrySet()) {
                Long userId = entry.getKey();
                SseEmitter emitter = entry.getValue();
                try {
                    emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
                } catch (IOException e) {
                    log.error("[SseService] userId={}",userId, e);
                    emitter.completeWithError(e);
                    emitterMap.remove(userId);
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        emitterMap.clear();
    }
}
