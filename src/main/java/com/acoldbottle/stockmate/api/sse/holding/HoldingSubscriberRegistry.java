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

    public void registerAllByPortfolioId(Long portfolioId) {
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioId(portfolioId);
        if (!holdings.isEmpty()) {
            holdings.stream()
                    .map(holding -> holding.getStock().getSymbol())
                    .forEach(symbol -> holdingSubscribersMap.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet())
                            .add(portfolioId));
        }
    }

    public void unregisterByPortfolioId(Long portfolioId) {
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioId(portfolioId);
        if (!holdings.isEmpty()) {
            holdings.stream()
                    .map(holding -> holding.getStock().getSymbol())
                    .forEach(symbol -> {
                        Set<Long> portfolioIds = holdingSubscribersMap.get(symbol);
                        portfolioIds.remove(portfolioId);
                        if (portfolioIds.isEmpty()) {
                            holdingSubscribersMap.remove(symbol);
                        }
                    });
        }
    }
}
