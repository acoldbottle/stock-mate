package com.acoldbottle.stockmate.api.profit.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingWithProfitRes;
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
    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ExecutorService cpuExecutor = Executors.newFixedThreadPool(2);

    public ProfitDTO calculateProfitInPortfolio(List<Holding> holdings) {
        final BigDecimal[] portfolioCurrentValue = {BigDecimal.ZERO};
        BigDecimal portfolioProfitRate = BigDecimal.ZERO;
        final BigDecimal[] portfolioProfitAmount = {BigDecimal.ZERO};

        List<HoldingProfitDTO> holdingProfitDTOList = holdings.stream()
                .map(
                        holding -> CompletableFuture.supplyAsync(() -> cacheService.getCurrentPrice(holding.getStock().getSymbol()), virtualExecutor)
                                .thenApply(currentPriceDTO -> getHoldingProfitDTOWithProfit(holding, currentPriceDTO))
                                .join()
                )
                .toList();

        holdingProfitDTOList
                .forEach(dto ->{
                    portfolioCurrentValue[0] = portfolioCurrentValue[0].add(dto.getTotalAmount());
                    portfolioProfitAmount[0] = portfolioProfitAmount[0].add(dto.getProfitAmount());
                });

        BigDecimal portfolioTotalPurchaseAmount = portfolioCurrentValue[0].subtract(portfolioProfitAmount[0]);
        portfolioProfitRate = calculateProfitRate(portfolioTotalPurchaseAmount, portfolioProfitAmount[0]);

        return ProfitDTO.builder()
                .portfolioCurrentValue(portfolioCurrentValue[0].setScale(2, RoundingMode.HALF_UP))
                .portfolioProfitRate(portfolioProfitRate)
                .portfolioProfitAmount(portfolioProfitAmount[0].setScale(2, RoundingMode.HALF_UP))
                .holdingList(holdingProfitDTOList)
                .build();
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
                .build();
    }

    private HoldingProfitDTO currentDTOIsNull(Holding holding) {
        String symbol = holding.getStock().getSymbol();
        String marketCode = holding.getStock().getMarketCode();
        BigDecimal avgPurchasePrice = holding.getPurchasePrice();
        int quantity = holding.getQuantity();
        BigDecimal totalAmount = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity));

        return HoldingProfitDTO.builder()
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

    private BigDecimal calculateProfitRate(BigDecimal totalPurchaseAmount, BigDecimal profitAmount) {
        BigDecimal profitRate = BigDecimal.ZERO;
        if (totalPurchaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            profitRate = profitAmount.divide(totalPurchaseAmount, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return profitRate;
    }


    public HoldingWithProfitRes getHoldingWithProfit(Holding holding) {
        CompletableFuture<CurrentPriceDTO> currentInfoFuture = CompletableFuture.supplyAsync(() ->
                cacheService.getCurrentPrice(holding.getStock().getSymbol()), virtualExecutor);

        return currentInfoFuture.thenApplyAsync(currentInfo -> calculateHoldingProfit(holding, currentInfo), cpuExecutor).join();
    }

    private HoldingWithProfitRes calculateHoldingProfit(Holding holding, CurrentPriceDTO currentInfo) {
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
        BigDecimal profitRate = calculateProfitRate(totalAmount, profitAmount);

        return HoldingWithProfitRes.builder()
                .holdingId(holding.getId())
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

    private HoldingWithProfitRes ifCurrentInfoIsNull(Holding holding) {
        String symbol = holding.getStock().getSymbol();
        String marketCode = holding.getStock().getMarketCode();
        BigDecimal avgPurchasePrice = holding.getPurchasePrice();
        int quantity = holding.getQuantity();
        BigDecimal totalAmount = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity));
        return HoldingWithProfitRes.builder()
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
