package com.acoldbottle.stockmate.api.sse.portfolio;

import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PortfolioUpdateDto {

    private Long portfolioId;

    private BigDecimal portfolioCurrentValue;

    private BigDecimal portfolioProfitAmount;

    private BigDecimal portfolioProfitRate;

    public static PortfolioUpdateDto from(Long portfolioId, ProfitDTO profitDto) {
        return PortfolioUpdateDto.builder()
                .portfolioId(portfolioId)
                .portfolioCurrentValue(profitDto.getPortfolioCurrentValue())
                .portfolioProfitAmount(profitDto.getPortfolioProfitAmount())
                .portfolioProfitRate(profitDto.getPortfolioProfitRate())
                .build();
    }
}
