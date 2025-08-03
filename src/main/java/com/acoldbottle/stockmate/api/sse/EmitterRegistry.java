package com.acoldbottle.stockmate.api.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmitterRegistry {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void save(Long userId, SseEmitter emitter) {
        emitterMap.put(userId, emitter);
    }

    public Optional<SseEmitter> findByUserId(Long userId) {
        SseEmitter emitter = emitterMap.get(userId);
        return Optional.ofNullable(emitter);
    }

    public Collection<SseEmitter> findAll() {
        return emitterMap.values();
    }

    public Set<Map.Entry<Long, SseEmitter>> findAllEntry() {
        return emitterMap.entrySet();
    }

    public SseEmitter delete(Long userId) {
        return emitterMap.remove(userId);
    }

    public void deleteAll() {
        emitterMap.clear();
    }
}
