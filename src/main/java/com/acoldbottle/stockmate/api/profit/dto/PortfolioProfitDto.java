package com.acoldbottle.stockmate.api.profit.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PortfolioProfitDto {

    private BigDecimal portfolioCurrentValue;
    private BigDecimal portfolioProfitAmount;
    private BigDecimal portfolioProfitRate;
}
