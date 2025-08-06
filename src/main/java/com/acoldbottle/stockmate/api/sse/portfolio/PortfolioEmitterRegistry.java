package com.acoldbottle.stockmate.api.sse.portfolio;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PortfolioEmitterRegistry {

    private final Map<Long, SseEmitter> portfolioEmitterMap = new ConcurrentHashMap<>();

    public Optional<SseEmitter> getEmitterByUserId(Long userId) {
        SseEmitter emitter = portfolioEmitterMap.get(userId);
        return Optional.ofNullable(emitter);
    }

    public void register(Long userId, SseEmitter emitter) {
        portfolioEmitterMap.put(userId, emitter);
    }

    public SseEmitter unregister(Long userId) {
        return portfolioEmitterMap.remove(userId);
    }
}
