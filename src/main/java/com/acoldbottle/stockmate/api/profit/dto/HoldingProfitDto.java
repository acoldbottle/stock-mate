package com.acoldbottle.stockmate.api.profit.dto;

import com.acoldbottle.stockmate.domain.holding.Holding;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class HoldingProfitDto {

    private Long holdingId;

    private BigDecimal currentPrice; // 현재가
    private BigDecimal rate; // 당일 대비 등락률
    private BigDecimal profitAmount; // 총 수익
    private BigDecimal profitRate; // 총 수익률
    private BigDecimal totalAmount; // 총 금액


    public static HoldingProfitDto withoutProfit(Holding holding) {
        return HoldingProfitDto.builder()
                .holdingId(holding.getId())
                .currentPrice(BigDecimal.ZERO)
                .rate(BigDecimal.ZERO)
                .profitAmount(BigDecimal.ZERO)
                .profitRate(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();
    }
}
