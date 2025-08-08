package com.acoldbottle.stockmate.api.sse.holding;

import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class HoldingSubscriberRegistry {

    private final Map<String, Set<HoldingSubscriber>> holdingSubscribersMap = new ConcurrentHashMap<>();
    private final HoldingRepository holdingRepository;

    public Set<HoldingSubscriber> getSubscribersBySymbol(String symbol) {
        return holdingSubscribersMap.get(symbol);
    }

    public void register(String symbol, Long userId, Long portfolioId) {
        holdingSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                .add(new HoldingSubscriber(userId, portfolioId));
    }

    public void registerAllByPortfolioId(Long userId, Long portfolioId) {
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioId(portfolioId);
        if (!holdings.isEmpty()) {
            holdings.stream()
                    .map(holding -> holding.getStock().getSymbol())
                    .forEach(symbol -> holdingSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                            .add(new HoldingSubscriber(userId, portfolioId)));
        }
    }

    public void unregister(String symbol, Long userId, Long portfolioId) {
        Set<HoldingSubscriber> subscribers = holdingSubscribersMap.get(symbol);
        if (subscribers != null) {
            subscribers.remove(new HoldingSubscriber(userId, portfolioId));
            if (subscribers.isEmpty()) {
                holdingSubscribersMap.remove(symbol);
            }
        }
    }

    public void unregisterByPortfolioId(Long userId, Long portfolioId) {
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioId(portfolioId);
        if (!holdings.isEmpty()) {
            holdings.stream()
                    .map(holding -> holding.getStock().getSymbol())
                    .forEach(symbol -> {
                        Set<HoldingSubscriber> portfolioIds = holdingSubscribersMap.get(symbol);
                        if (portfolioIds != null) {
                            portfolioIds.remove(new HoldingSubscriber(userId, portfolioId));
                            if (portfolioIds.isEmpty()) {
                                holdingSubscribersMap.remove(symbol);
                            }
                        }
                    });
        }
    }

    public boolean isRegisteredPortfolioId(Long portfolioId) {
        return holdingSubscribersMap.values().stream()
                .flatMap(Set::stream)
                .anyMatch(subscriber -> subscriber.getPortfolioId().equals(portfolioId));
    }
}
