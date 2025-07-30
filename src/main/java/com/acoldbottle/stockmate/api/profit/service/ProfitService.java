package com.acoldbottle.stockmate.api.profit.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import com.acoldbottle.stockmate.domain.holding.Holding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.acoldbottle.stockmate.api.profit.dto.ProfitDTO.HoldingProfitDTO;

@Service
@RequiredArgsConstructor
public class ProfitService {

    private final CurrentPriceCacheService cacheService;

    public ProfitDTO calculateProfitInPortfolio(List<Holding> holdings) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<HoldingProfitDTO>> futures = holdings.stream()
                    .map(
                            holding -> CompletableFuture.supplyAsync(() -> cacheService.getCurrentPrice(holding.getStock().getSymbol()), executor)
                                    .thenApply(currentPriceDTO -> getHoldingProfitDTOWithProfit(holding, currentPriceDTO))
                    )
                    .toList();
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();
            List<HoldingProfitDTO> holdingProfitDTOList = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            BigDecimal portfolioCurrentValue = BigDecimal.ZERO;
            BigDecimal portfolioProfitAmount = BigDecimal.ZERO;
            for (HoldingProfitDTO holdingProfitDTO : holdingProfitDTOList) {
                portfolioCurrentValue = portfolioCurrentValue.add(holdingProfitDTO.getTotalAmount());
                portfolioProfitAmount = portfolioProfitAmount.add(holdingProfitDTO.getProfitAmount());
            }
            BigDecimal portfolioTotalPurchaseAmount = portfolioCurrentValue.subtract(portfolioProfitAmount);
            BigDecimal portfolioProfitRate = calculateProfitRate(portfolioTotalPurchaseAmount, portfolioProfitAmount);

            return ProfitDTO.builder()
                    .portfolioCurrentValue(portfolioCurrentValue)
                    .portfolioProfitRate(portfolioProfitRate)
                    .portfolioProfitAmount(portfolioProfitAmount)
                    .holdingList(holdingProfitDTOList)
                    .build();
        }
    }

    private HoldingProfitDTO getHoldingProfitDTOWithProfit(Holding holding, CurrentPriceDTO currentPriceDTO) {
        if (currentPriceDTO == null) {
            return currentDTOIsNull(holding);
        }
        String symbol = holding.getStock().getSymbol();
        String marketCode = holding.getStock().getMarketCode();
        BigDecimal avgPurchasePrice = holding.getPurchasePrice();
        int quantity = holding.getQuantity();
        BigDecimal currentPrice = currentPriceDTO.getLast();
        BigDecimal rate = currentPriceDTO.getRate();
        BigDecimal totalPurchaseAmount = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalAmount = currentPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal profitAmount = currentPrice.subtract(avgPurchasePrice).multiply(BigDecimal.valueOf(quantity));
        BigDecimal profitRate = calculateProfitRate(totalPurchaseAmount, profitAmount);

        return HoldingProfitDTO.builder()
                .holdingId(holding.getId())
                .symbol(symbol)
                .marketCode(marketCode)
                .avgPurchasePrice(avgPurchasePrice)
                .quantity(quantity)
                .currentPrice(currentPrice)
                .rate(rate)
                .profitAmount(profitAmount)
                .profitRate(profitRate)
                .totalAmount(totalAmount)
                .korName(holding.getStock().getKorName())
                .engName(holding.getStock().getEngName())
                .build();
    }

    private HoldingProfitDTO currentDTOIsNull(Holding holding) {
        String symbol = holding.getStock().getSymbol();
        String marketCode = holding.getStock().getMarketCode();
        BigDecimal avgPurchasePrice = holding.getPurchasePrice();
        int quantity = holding.getQuantity();
        BigDecimal totalAmount = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity));

        return HoldingProfitDTO.builder()
                .holdingId(holding.getId())
                .symbol(symbol)
                .marketCode(marketCode)
                .avgPurchasePrice(avgPurchasePrice)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .currentPrice(BigDecimal.ZERO)
                .rate(BigDecimal.ZERO)
                .profitAmount(BigDecimal.ZERO)
                .profitRate(BigDecimal.ZERO)
                .korName(holding.getStock().getKorName())
                .engName(holding.getStock().getEngName())
                .build();
    }

    private BigDecimal calculateProfitRate(BigDecimal totalPurchaseAmount, BigDecimal profitAmount) {
        BigDecimal profitRate = BigDecimal.ZERO;
        if (totalPurchaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            profitRate = profitAmount.divide(totalPurchaseAmount, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return profitRate;
    }
}
