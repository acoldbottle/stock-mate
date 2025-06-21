package com.acoldbottle.stockmate.api.holding.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class HoldingGetWithProfitRes {

    private String symbol;
    private String marketCode;
    private BigDecimal avgPurchasePrice; // 평균 매수가
    private int quantity; // 수량
    private BigDecimal totalAmount; // 총 매수 금액
    private BigDecimal currentPrice; // 현재가
    private BigDecimal rate; // 당일 대비 등락률
    private BigDecimal profitAmount; // 총 수익
    private BigDecimal profitRate; // 총 수익률

}
