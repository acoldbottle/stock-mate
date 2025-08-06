package com.acoldbottle.stockmate.api.sse.watchlist;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WatchlistEmitterRegistry {

    private final Map<Long, SseEmitter> watchlistEmitterMap = new ConcurrentHashMap<>();

    public Optional<SseEmitter> getEmitterByUserId(Long userId) {
        SseEmitter emitter = watchlistEmitterMap.get(userId);
        return Optional.ofNullable(emitter);
    }

    public void register(Long userId, SseEmitter emitter) {
        watchlistEmitterMap.put(userId, emitter);
    }

    public SseEmitter unregister(Long userId) {
        return watchlistEmitterMap.remove(userId);
    }
}
