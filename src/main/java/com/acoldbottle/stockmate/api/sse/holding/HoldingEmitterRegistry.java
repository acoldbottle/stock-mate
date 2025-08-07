package com.acoldbottle.stockmate.api.sse.holding;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HoldingEmitterRegistry {

    private final Map<Long, SseEmitter> holdingEmitterMap = new ConcurrentHashMap<>();

    public Optional<SseEmitter> getEmitterByUserId(Long userId) {
        SseEmitter emitter = holdingEmitterMap.get(userId);
        return Optional.ofNullable(emitter);
    }

    public void register(Long userId, SseEmitter newEmitter) {
        holdingEmitterMap.put(userId, newEmitter);
    }

    public SseEmitter unregister(Long userId) {
        return holdingEmitterMap.remove(userId);
    }
}
