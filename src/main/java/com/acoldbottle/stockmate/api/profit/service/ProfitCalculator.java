package com.acoldbottle.stockmate.api.profit.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.api.holding.service.HoldingManager;
import com.acoldbottle.stockmate.api.profit.dto.HoldingProfitDto;
import com.acoldbottle.stockmate.api.profit.dto.PortfolioProfitDto;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.stock.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfitCalculator {

    private final CurrentPriceCacheService cacheService;
    private final HoldingManager holdingManager;

    public Map<Long, PortfolioProfitDto> portfolioProfit(List<Portfolio> portfolioList) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Map<Long, PortfolioProfitDto> portfolioProfitDtoMap = new ConcurrentHashMap<>();
            List<CompletableFuture<Void>> futures = portfolioList.stream()
                    .map(portfolio ->
                            CompletableFuture.runAsync(() -> {
                                PortfolioProfitDto portfolioProfitDto = calculatePortfolio(portfolio);
                                portfolioProfitDtoMap.put(portfolio.getId(), portfolioProfitDto);
                            }, executor))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            return portfolioProfitDtoMap;
        }
    }

    public Map<Long, HoldingProfitDto> holdingListProfit(List<Holding> holdingList) {
        return holdingList.stream()
                .map(this::calculateHolding)
                .collect(Collectors.toMap(
                        HoldingProfitDto::getHoldingId,
                        holdingProfitDto -> holdingProfitDto
                ));
    }

    private PortfolioProfitDto calculatePortfolio(Portfolio portfolio) {
        List<Holding> holdingList = holdingManager.getHoldingListIn(portfolio);
        List<HoldingProfitDto> holdingProfitDtoList = holdingList.stream()
                .map(this::calculateHolding)
                .toList();

        BigDecimal portfolioCurrentValue = BigDecimal.ZERO;
        BigDecimal portfolioProfitAmount = BigDecimal.ZERO;

        for (HoldingProfitDto holdingProfitDto : holdingProfitDtoList) {
            portfolioCurrentValue = calculatePortfolioCurrentValue(portfolioCurrentValue, holdingProfitDto.getTotalAmount());
            portfolioProfitAmount = calculatePortfolioProfitAmount(portfolioProfitAmount, holdingProfitDto.getProfitAmount());
        }
        BigDecimal portfolioProfitRate = calculatePortfolioProfitRate(portfolioCurrentValue, portfolioProfitAmount);

        return PortfolioProfitDto.builder()
                .portfolioId(portfolio.getId())
                .portfolioCurrentValue(portfolioCurrentValue)
                .portfolioProfitAmount(portfolioProfitAmount)
                .portfolioProfitRate(portfolioProfitRate)
                .build();
    }

    private HoldingProfitDto calculateHolding(Holding holding) {
        Stock stock = holding.getStock();
        CurrentPriceDTO currentPriceDto = cacheService.getCurrentPrice(stock.getSymbol());

        if (currentPriceDto == null) {
            return HoldingProfitDto.withoutProfit(holding);
        }

        BigDecimal currentPrice = currentPriceDto.getLast();
        BigDecimal rate = currentPriceDto.getRate();
        BigDecimal profitAmount = calculateHoldingProfitAmount(holding, currentPrice);
        BigDecimal profitRate = calculateHoldingProfitRate(holding, profitAmount);
        BigDecimal totalAmount = calculateHoldingTotalAmount(holding, profitAmount);

        return HoldingProfitDto.builder()
                .holdingId(holding.getId())
                .currentPrice(currentPrice)
                .rate(rate)
                .profitAmount(profitAmount)
                .profitRate(profitRate)
                .totalAmount(totalAmount)
                .build();
    }

    private BigDecimal calculatePortfolioCurrentValue(BigDecimal portfolioCurrentValue, BigDecimal holdingTotalAmount) {
        return portfolioCurrentValue.add(holdingTotalAmount);
    }

    private BigDecimal calculatePortfolioProfitAmount(BigDecimal portfolioProfitAmount, BigDecimal holdingProfitAmount) {
        return portfolioProfitAmount.add(holdingProfitAmount);
    }

    private BigDecimal calculatePortfolioProfitRate(BigDecimal portfolioCurrentValue, BigDecimal portfolioProfitAmount) {
        BigDecimal portfolioPurchaseAmount = portfolioCurrentValue.subtract(portfolioProfitAmount);
        BigDecimal portfolioProfitRate = BigDecimal.ZERO;
        if (portfolioPurchaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            portfolioProfitRate = portfolioProfitAmount.divide(portfolioPurchaseAmount, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return portfolioProfitRate;
    }


    private BigDecimal calculateHoldingProfitAmount(Holding holding, BigDecimal currentPrice) {
        return currentPrice.subtract(holding.getPurchasePrice()).multiply(BigDecimal.valueOf(holding.getQuantity()));
    }

    private BigDecimal calculateHoldingProfitRate(Holding holding, BigDecimal profitAmount) {
        BigDecimal profitRate = BigDecimal.ZERO;
        BigDecimal purchaseAmount = holding.getPurchasePrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
        if (purchaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            profitRate = profitAmount.divide(purchaseAmount, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return profitRate;
    }

    private BigDecimal calculateHoldingTotalAmount(Holding holding, BigDecimal profitAmount) {
        BigDecimal purchasePrice = holding.getPurchasePrice();
        int quantity = holding.getQuantity();
        return purchasePrice.multiply(BigDecimal.valueOf(quantity)).add(profitAmount);
    }

}
