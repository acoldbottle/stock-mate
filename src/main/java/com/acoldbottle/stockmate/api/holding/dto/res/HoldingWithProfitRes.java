package com.acoldbottle.stockmate.api.holding.dto.res;

import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
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

    public static HoldingWithProfitRes from(ProfitDTO.HoldingProfitDTO holdingProfitDTO) {
        return HoldingWithProfitRes.builder()
                .holdingId(holdingProfitDTO.getHoldingId())
                .symbol(holdingProfitDTO.getSymbol())
                .marketCode(holdingProfitDTO.getMarketCode())
                .avgPurchasePrice(holdingProfitDTO.getAvgPurchasePrice())
                .quantity(holdingProfitDTO.getQuantity())
                .totalAmount(holdingProfitDTO.getTotalAmount().setScale(2, RoundingMode.HALF_UP))
                .currentPrice(holdingProfitDTO.getCurrentPrice().setScale(2, RoundingMode.HALF_UP))
                .rate(holdingProfitDTO.getRate())
                .profitAmount(holdingProfitDTO.getProfitAmount().setScale(2, RoundingMode.HALF_UP))
                .profitRate(holdingProfitDTO.getProfitRate())
                .build();
    }
}
