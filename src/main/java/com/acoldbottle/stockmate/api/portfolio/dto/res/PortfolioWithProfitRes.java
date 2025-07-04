package com.acoldbottle.stockmate.api.portfolio.dto.res;

import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
public class PortfolioWithProfitRes {

    private Long portfolioId;
    private String title;
    private BigDecimal portfolioCurrentValue;
    private BigDecimal portfolioProfitAmount;
    private BigDecimal portfolioProfitRate;


    public static PortfolioWithProfitRes from(Portfolio portfolio, ProfitDTO profitDTO) {
        return PortfolioWithProfitRes.builder()
                .portfolioId(portfolio.getId())
                .title(portfolio.getTitle())
                .portfolioCurrentValue(profitDTO.getPortfolioCurrentValue().setScale(2, RoundingMode.HALF_UP))
                .portfolioProfitAmount(profitDTO.getPortfolioProfitAmount().setScale(2, RoundingMode.HALF_UP))
                .portfolioProfitRate(profitDTO.getPortfolioProfitRate())
                .build();
    }
}
