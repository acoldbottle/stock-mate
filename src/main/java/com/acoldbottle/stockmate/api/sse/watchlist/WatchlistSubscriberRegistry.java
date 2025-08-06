package com.acoldbottle.stockmate.api.sse.watchlist;

import com.acoldbottle.stockmate.domain.watchitem.WatchItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WatchlistSubscriberRegistry {

    private final Map<String, Set<Long>> watchlistSubscribersMap = new ConcurrentHashMap<>();
    private final WatchItemRepository watchItemRepository;

    public Set<Long> getSubscribersBySymbol(String symbol) {
        return watchlistSubscribersMap.get(symbol);
    }

    public void register(Long userId, String symbol) {
        watchlistSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                .add(userId);
    }

    public void registerAllByUserId(Long userId) {
        List<String> watchItemSymbols = watchItemRepository.findSymbolsByUserId(userId);
        watchItemSymbols.forEach(symbol ->
                watchlistSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                        .add(userId));
    }

    public void unregister(Long userId, String symbol) {
        Set<Long> users = watchlistSubscribersMap.get(symbol);
        users.remove(userId);
        if (users.isEmpty()) watchlistSubscribersMap.remove(symbol);
    }

    public void unregisterByUserId(Long userId) {
        List<String> symbols = watchItemRepository.findSymbolsByUserId(userId);
        if (!symbols.isEmpty()) {
            symbols.forEach(symbol -> {
                Set<Long> userIds = watchlistSubscribersMap.get(symbol);
                userIds.remove(userId);
                if (userIds.isEmpty()) {
                    watchlistSubscribersMap.remove(symbol);
                }
            });
        }
    }
}
