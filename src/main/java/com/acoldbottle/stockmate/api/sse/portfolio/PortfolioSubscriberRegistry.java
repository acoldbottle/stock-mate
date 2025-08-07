package com.acoldbottle.stockmate.api.sse.portfolio;

import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
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
    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;

    public Set<Long> getSubscribersBySymbol(String symbol) {
        return portfolioSubscribersMap.get(symbol);
    }

    public List<Holding> getHoldingsBySubscriber(Long portfolioId) {
        return holdingRepository.findAllWithStockByPortfolioId(portfolioId);
    }

    public void registerAllByUserId(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findAllByUserId(userId);
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioIn(portfolios);
        holdings.forEach(holding ->
            portfolioSubscribersMap.computeIfAbsent(holding.getStock().getSymbol(), k -> ConcurrentHashMap.newKeySet())
                    .add(holding.getPortfolio().getId()));
    }

    public void unregister(Long portfolioId) {
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolioId(portfolioId);
        if (!holdings.isEmpty()) {
            holdings.stream()
                    .map(holding -> holding.getStock().getSymbol())
                    .forEach(symbol -> {
                        Set<Long> portfolioIdSet = portfolioSubscribersMap.get(symbol);
                        if (portfolioIdSet != null) {
                            portfolioIdSet.remove(portfolioId);
                            if (portfolioIdSet.isEmpty()) {
                                portfolioSubscribersMap.remove(symbol);
                            }
                        }
                    });
        }
    }

    public void unregisterByUserId(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findAllByUserId(userId);
        if (!portfolios.isEmpty()) {
            Set<Long> portfolioIds = portfolios.stream()
                    .map(Portfolio::getId)
                    .collect(Collectors.toSet());
            portfolioSubscribersMap.forEach((symbol, portfolioIdSet) -> {
                portfolioIdSet.removeIf(portfolioIds::contains);
                if (portfolioIdSet.isEmpty()) {
                    portfolioSubscribersMap.remove(symbol);
                }
            });
        }
    }
}
