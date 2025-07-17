package com.acoldbottle.stockmate.api.profit.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ProfitDTO {

    private BigDecimal portfolioCurrentValue;
    private BigDecimal portfolioProfitRate;
    private BigDecimal portfolioProfitAmount;
    private List<HoldingProfitDTO> holdingList;
    @Getter
    @Builder
    public static class HoldingProfitDTO {
        private Long holdingId;
        private String symbol;
        private String marketCode;
        private BigDecimal avgPurchasePrice; // 평균 매수가
        private int quantity; // 수량
        private BigDecimal currentPrice; // 현재가
        private BigDecimal rate; // 당일 대비 등락률
        private BigDecimal profitAmount; // 총 수익
        private BigDecimal profitRate; // 총 수익률
        private BigDecimal totalAmount; // 총 금액

        private String korName;
        private String engName;
    }
}
