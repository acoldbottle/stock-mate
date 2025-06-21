package com.acoldbottle.stockmate.api.holding.service;

import com.acoldbottle.stockmate.api.holding.dto.res.HoldingGetWithProfitRes;
import com.acoldbottle.stockmate.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.domain.holding.Holding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class HoldingProfitService {

    private final CurrentPriceCacheService currentPriceCacheService;
    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ExecutorService cpuExecutor = Executors.newFixedThreadPool(2);


    public HoldingGetWithProfitRes getHoldingWithProfit(Holding holding) {
        CompletableFuture<CurrentPriceDTO> currentInfoFuture = CompletableFuture.supplyAsync(() ->
                currentPriceCacheService.getCurrentPrice(holding.getStock().getSymbol()), virtualExecutor);

        return currentInfoFuture.thenApplyAsync(currentInfo -> calculateHoldingProfit(holding, currentInfo), cpuExecutor).join();
    }

    private static HoldingGetWithProfitRes calculateHoldingProfit(Holding holding, CurrentPriceDTO currentInfo) {
        if (currentInfo == null) {
            return ifCurrentInfoIsNull(holding);
        }
        String symbol = holding.getStock().getSymbol();
        String marketCode = holding.getStock().getMarketCode();
        BigDecimal avgPurchasePrice = holding.getPurchasePrice();
        int quantity = holding.getQuantity();
        BigDecimal totalAmount = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal currentPrice = currentInfo.getLast();
        BigDecimal rate = currentInfo.getRate();
        BigDecimal profitAmount = currentPrice.subtract(avgPurchasePrice).multiply(BigDecimal.valueOf(quantity));
        BigDecimal profitRate = BigDecimal.ZERO;
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            profitRate = profitAmount.divide(totalAmount, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return HoldingGetWithProfitRes.builder()
                .symbol(symbol)
                .marketCode(marketCode)
                .avgPurchasePrice(avgPurchasePrice)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .currentPrice(currentPrice)
                .rate(rate)
                .profitAmount(profitAmount)
                .profitRate(profitRate)
                .build();
    }

    private static HoldingGetWithProfitRes ifCurrentInfoIsNull(Holding holding) {
        String symbol = holding.getStock().getSymbol();
        String marketCode = holding.getStock().getMarketCode();
        BigDecimal avgPurchasePrice = holding.getPurchasePrice();
        int quantity = holding.getQuantity();
        BigDecimal totalAmount = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity));
        return HoldingGetWithProfitRes.builder()
                .symbol(symbol)
                .marketCode(marketCode)
                .avgPurchasePrice(avgPurchasePrice)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .currentPrice(BigDecimal.ZERO)
                .rate(BigDecimal.ZERO)
                .profitAmount(BigDecimal.ZERO)
                .profitRate(BigDecimal.ZERO)
                .build();
    }
}
