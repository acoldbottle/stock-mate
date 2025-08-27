package com.acoldbottle.stockmate.api.sse.watchlist;

import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WatchlistSubscriberRegistry {

    private final Map<String, Set<Long>> watchlistSubscribersMap = new ConcurrentHashMap<>();

    public Set<Long> getSubscribersBySymbol(String symbol) {
        return watchlistSubscribersMap.getOrDefault(symbol, Collections.emptySet());
    }

    public void register(Long userId, String symbol) {
        watchlistSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                .add(userId);
    }

    public void registerAll(Long userId, List<WatchItem> watchlist) {
        if (watchlist.isEmpty()) return;
        watchlist.forEach(w ->
            watchlistSubscribersMap.computeIfAbsent(w.getStock().getSymbol(), k -> ConcurrentHashMap.newKeySet())
                    .add(userId));
    }

    public void unregister(Long userId, String symbol) {
        Set<Long> users = watchlistSubscribersMap.get(symbol);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) watchlistSubscribersMap.remove(symbol);
        }
    }

    public void unregisterAll(Long userId, List<WatchItem> watchlist) {
        if (watchlist.isEmpty()) return;
        watchlist.forEach(w -> {
            String key = w.getStock().getSymbol();
            Set<Long> userIds = watchlistSubscribersMap.get(key);
            if (userIds != null) {
                userIds.remove(userId);
                if (userIds.isEmpty()) {
                    watchlistSubscribersMap.remove(key);
                }
            }
        });
    }
}
