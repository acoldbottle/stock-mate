package com.acoldbottle.stockmate.api.holding.dto.res;

import com.acoldbottle.stockmate.api.profit.dto.HoldingProfitDto;
import com.acoldbottle.stockmate.domain.holding.Holding;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
public class HoldingWithProfitRes {

    private Long holdingId;
    private String symbol;
    private String marketCode;
    private BigDecimal avgPurchasePrice; // 평균 매수가
    private int quantity; // 수량
    private BigDecimal totalAmount; // 총 매수 금액
    private BigDecimal currentPrice; // 현재가
    private BigDecimal rate; // 당일 대비 등락률
    private BigDecimal profitAmount; // 총 수익
    private BigDecimal profitRate; // 총 수익률

    public static HoldingWithProfitRes from(Holding holding, HoldingProfitDto holdingProfitDto) {
        return HoldingWithProfitRes.builder()
                .holdingId(holding.getId())
                .symbol(holding.getStock().getSymbol())
                .marketCode(holding.getStock().getMarketCode())
                .avgPurchasePrice(holding.getPurchasePrice())
                .quantity(holding.getQuantity())
                .totalAmount(holdingProfitDto.getTotalAmount().setScale(2, RoundingMode.HALF_UP))
                .currentPrice(holdingProfitDto.getCurrentPrice().setScale(2, RoundingMode.HALF_UP))
                .rate(holdingProfitDto.getRate())
                .profitAmount(holdingProfitDto.getProfitAmount().setScale(2, RoundingMode.HALF_UP))
                .profitRate(holdingProfitDto.getProfitRate())
                .build();
    }
}
