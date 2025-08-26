package com.acoldbottle.stockmate.api.sse.holding;

import com.acoldbottle.stockmate.domain.holding.Holding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class HoldingSubscriberRegistry {

    private final Map<String, Set<HoldingSubscriber>> holdingSubscribersMap = new ConcurrentHashMap<>();

    public Set<HoldingSubscriber> getSubscribersBySymbol(String symbol) {
        return holdingSubscribersMap.get(symbol);
    }

    public void register(Long userId, Long portfolioId, String symbol) {
        holdingSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                .add(new HoldingSubscriber(userId, portfolioId));
    }

    public void registerAll(Long userId, Long portfolioId, List<Holding> holdingList) {
        if (holdingList.isEmpty()) return;

        HoldingSubscriber holdingSubscriber = new HoldingSubscriber(userId, portfolioId);
        holdingList.stream()
                .map(holding -> holding.getStock().getSymbol())
                .forEach(symbol -> holdingSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                        .add(holdingSubscriber));
    }

    public void unregister(Long userId, Long portfolioId, String symbol) {
        Set<HoldingSubscriber> subscribers = holdingSubscribersMap.get(symbol);
        if (subscribers != null) {
            subscribers.remove(new HoldingSubscriber(userId, portfolioId));
            if (subscribers.isEmpty()) {
                holdingSubscribersMap.remove(symbol);
            }
        }
    }

    public void unregisterAll(Long userId, Long portfolioId, List<Holding> holdingList) {
        if (holdingList.isEmpty()) return;

        HoldingSubscriber holdingSubscriber = new HoldingSubscriber(userId, portfolioId);
        holdingList.stream()
                .map(holding -> holding.getStock().getSymbol())
                .forEach(symbol -> {
                    Set<HoldingSubscriber> holdingSubscribers = holdingSubscribersMap.get(symbol);
                    if (holdingSubscribers != null) {
                        holdingSubscribers.remove(holdingSubscriber);
                        if (holdingSubscribers.isEmpty()) {
                            holdingSubscribersMap.remove(symbol);
                        }
                    }
                });
    }

    public boolean isRegisteredPortfolioId(Long portfolioId) {
        return holdingSubscribersMap.values().stream()
                .flatMap(Set::stream)
                .anyMatch(subscriber -> subscriber.getPortfolioId().equals(portfolioId));
    }
}
