package com.acoldbottle.stockmate.api.sse.holding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class HoldingSseService {

    private final HoldingEmitterRegistry emitterRegistry;
    private final HoldingSubscriberRegistry subscriberRegistry;

    public SseEmitter connect(Long userId, Long portfolioId) {
        Optional<SseEmitter> isExistEmitter = emitterRegistry.getEmitterByUserId(userId);
        if (isExistEmitter.isPresent()) {
            return isExistEmitter.get();
        }
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        emitterRegistry.register(userId, newEmitter);
        subscriberRegistry.registerAllByPortfolioId(portfolioId);
        log.info("[HoldingSseService] user={}, portfolioId={} --> connect", userId, portfolioId);
        newEmitter.onCompletion(() -> {
            emitterRegistry.unregister(userId);
            subscriberRegistry.unregisterByPortfolioId(portfolioId);
            log.info("[HoldingSseService] user={}, portfolioId={} --> connection closed", userId, portfolioId);
        });
        newEmitter.onError((e) -> {
            emitterRegistry.unregister(userId);
            subscriberRegistry.unregisterByPortfolioId(portfolioId);
            log.error("[HoldingSseService] user={}, portfolioId={} --> connection Error!!", userId, portfolioId, e);
        });
        return newEmitter;
    }

    public void disconnect(Long userId, Long portfolioId) {
        subscriberRegistry.unregisterByPortfolioId(portfolioId);
        SseEmitter unregisteredEmitter = emitterRegistry.unregister(userId);
        if (unregisteredEmitter != null) {
            unregisteredEmitter.complete();
        }
    }
}
