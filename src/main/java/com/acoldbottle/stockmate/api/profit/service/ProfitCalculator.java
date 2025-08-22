package com.acoldbottle.stockmate.api.profit.service;

import com.acoldbottle.stockmate.api.profit.dto.HoldingCurrentInfoDto;
import com.acoldbottle.stockmate.api.profit.dto.HoldingProfitDto;
import com.acoldbottle.stockmate.api.profit.dto.PortfolioProfitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfitCalculator {

    public Map<Long, HoldingProfitDto> holdingListProfit(List<HoldingCurrentInfoDto> holdingList) {
        return holdingList.stream()
                .map(this::calculateHolding)
                .collect(Collectors.toMap(
                        HoldingProfitDto::getHoldingId,
                        holdingProfitDto -> holdingProfitDto
                ));
    }

    public PortfolioProfitDto portfolioProfit(List<HoldingCurrentInfoDto> holdingList) {
        if (holdingList == null || holdingList.isEmpty()) {
            return PortfolioProfitDto.builder()
                    .portfolioCurrentValue(BigDecimal.ZERO)
                    .portfolioProfitAmount(BigDecimal.ZERO)
                    .portfolioProfitRate(BigDecimal.ZERO)
                    .build();
        }

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
                .portfolioCurrentValue(portfolioCurrentValue)
                .portfolioProfitAmount(portfolioProfitAmount)
                .portfolioProfitRate(portfolioProfitRate)
                .build();
    }

    private HoldingProfitDto calculateHolding(HoldingCurrentInfoDto holding) {
        BigDecimal currentPrice = holding.getCurrentPrice();
        BigDecimal rate = holding.getRate();
        BigDecimal profitAmount = calculateHoldingProfitAmount(holding);
        BigDecimal profitRate = calculateHoldingProfitRate(holding, profitAmount);
        BigDecimal totalAmount = calculateHoldingTotalAmount(holding, profitAmount);

        return HoldingProfitDto.builder()
                .holdingId(holding.getHoldingId())
                .currentPrice(currentPrice)
                .rate(rate)
                .profitAmount(profitAmount)
                .profitRate(profitRate)
                .totalAmount(totalAmount)
                .build();
    }

    private BigDecimal calculateHoldingProfitAmount(HoldingCurrentInfoDto holding) {
        return holding.getCurrentPrice().subtract(holding.getAvgPurchasePrice()).multiply(BigDecimal.valueOf(holding.getQuantity()));
    }

    private BigDecimal calculateHoldingProfitRate(HoldingCurrentInfoDto holding, BigDecimal profitAmount) {
        BigDecimal profitRate = BigDecimal.ZERO;
        BigDecimal purchaseAmount = holding.getAvgPurchasePrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
        if (purchaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            profitRate = profitAmount.divide(purchaseAmount, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return profitRate;
    }

    private BigDecimal calculateHoldingTotalAmount(HoldingCurrentInfoDto holding, BigDecimal profitAmount) {
        BigDecimal purchasePrice = holding.getAvgPurchasePrice();
        int quantity = holding.getQuantity();
        return purchasePrice.multiply(BigDecimal.valueOf(quantity)).add(profitAmount);
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


}
