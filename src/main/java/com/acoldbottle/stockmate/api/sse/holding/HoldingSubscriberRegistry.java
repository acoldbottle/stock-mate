package com.acoldbottle.stockmate.api.sse.holding;

import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class HoldingSubscriberRegistry {

    private final Map<String, Set<Long>> holdingSubscribersMap = new ConcurrentHashMap<>();
    private final HoldingRepository holdingRepository;

    public Set<Long> getSubscribersBySymbol(String symbol) {
        return holdingSubscribersMap.get(symbol);
    }

    public void register(String symbol, Long portfolioId) {
        holdingSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                .add(portfolioId);
    }

    public void registerAllByPortfolioId(Long portfolioId) {
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioId(portfolioId);
        if (!holdings.isEmpty()) {
            holdings.stream()
                    .map(holding -> holding.getStock().getSymbol())
                    .forEach(symbol -> holdingSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                            .add(portfolioId));
        }
    }

    public void unregister(String symbol, Long portfolioId) {
        Set<Long> subscribers = holdingSubscribersMap.get(symbol);
        if (subscribers != null) {
            subscribers.remove(portfolioId);
            if (subscribers.isEmpty()) {
                holdingSubscribersMap.remove(symbol);
            }
        }
    }

    public void unregisterByPortfolioId(Long portfolioId) {
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioId(portfolioId);
        if (!holdings.isEmpty()) {
            holdings.stream()
                    .map(holding -> holding.getStock().getSymbol())
                    .forEach(symbol -> {
                        Set<Long> portfolioIds = holdingSubscribersMap.get(symbol);
                        if (portfolioIds != null) {
                            portfolioIds.remove(portfolioId);
                            if (portfolioIds.isEmpty()) {
                                holdingSubscribersMap.remove(symbol);
                            }
                        }
                    });
        }
    }
}
