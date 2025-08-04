package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.watchitem.WatchItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SubscriberRegistry {

    private final Map<String, Set<Long>> symbolSubscribersMap = new ConcurrentHashMap<>();
    private final HoldingRepository holdingRepository;
    private final WatchItemRepository watchItemRepository;

    public void save(Long userId, String symbol) {
        symbolSubscribersMap
                .computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                .add(userId);
    }

    public void saveAllByUserId(Long userId) {
        Set<String> symbolSet = new HashSet<>();
        List<String> holdingSymbolList = holdingRepository.findSymbolsByUserId(userId);
        List<String> watchItemSymbolList = watchItemRepository.findSymbolsByUserId(userId);

        symbolSet.addAll(holdingSymbolList);
        symbolSet.addAll(watchItemSymbolList);

        symbolSet.forEach(symbol ->
                symbolSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet()).add(userId)
        );
    }

    public void delete(Long userId, String symbol) {
        boolean isHoldingExists = holdingRepository.existsByUserIdAndSymbol(userId, symbol);
        boolean isWatchItemExists = watchItemRepository.existsByUserIdAndSymbol(userId, symbol);
        if (!isHoldingExists && !isWatchItemExists) {
            Set<Long> userSet = symbolSubscribersMap.get(symbol);
            userSet.remove(userId);
            if (userSet.isEmpty()) symbolSubscribersMap.remove(symbol);
        }
    }

    public void deleteByUserId(Long userId) {
        symbolSubscribersMap.forEach((symbol, users) -> users.remove(userId));
        symbolSubscribersMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public void deleteAll() {
        symbolSubscribersMap.clear();
    }
}
