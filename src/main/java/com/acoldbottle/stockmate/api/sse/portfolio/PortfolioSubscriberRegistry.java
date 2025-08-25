package com.acoldbottle.stockmate.api.sse.portfolio;

import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PortfolioSubscriberRegistry {

    private final Map<String, Set<Long>> portfolioSubscribersMap = new ConcurrentHashMap<>();

    public List<Long> getSubscribersBySymbol(String symbol) {
        return portfolioSubscribersMap.get(symbol).stream().toList();
    }

    public void registerAll(List<Holding> holdings) {
        holdings.forEach(holding ->
            portfolioSubscribersMap.computeIfAbsent(holding.getStock().getSymbol(), k -> ConcurrentHashMap.newKeySet())
                    .add(holding.getPortfolio().getId()));
    }

    public void unregister(Long portfolioId, List<String> symbolList) {
        symbolList.forEach(symbol -> {
            Set<Long> portfolioIdSet = portfolioSubscribersMap.get(symbol);
            if (portfolioIdSet != null) {
                portfolioIdSet.remove(portfolioId);
                if (portfolioIdSet.isEmpty()) {
                    portfolioSubscribersMap.remove(symbol);
                }
            }
        });
    }

    public void unregisterAll(List<Portfolio> portfolios) {
        if (!portfolios.isEmpty()) {
            Set<Long> portfolioIds = portfolios.stream()
                    .map(Portfolio::getId)
                    .collect(Collectors.toSet());
            portfolioSubscribersMap.forEach((symbol, portfolioIdSet) -> {
                portfolioSubscribersMap.computeIfPresent(symbol, (s, set) -> {
                    set.removeIf(portfolioIds::contains);
                    return set.isEmpty() ? null : set;
                });
            });
        }
    }
}
