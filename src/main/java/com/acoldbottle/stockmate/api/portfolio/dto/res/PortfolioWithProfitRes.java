package com.acoldbottle.stockmate.api.portfolio.dto.res;

import com.acoldbottle.stockmate.api.profit.dto.PortfolioProfitDto;
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


    public static PortfolioWithProfitRes from(Portfolio portfolio, PortfolioProfitDto portfolioProfitDto) {
        return PortfolioWithProfitRes.builder()
                .portfolioId(portfolio.getId())
                .title(portfolio.getTitle())
                .portfolioCurrentValue(portfolioProfitDto.getPortfolioCurrentValue().setScale(2, RoundingMode.HALF_UP))
                .portfolioProfitAmount(portfolioProfitDto.getPortfolioProfitAmount().setScale(2, RoundingMode.HALF_UP))
                .portfolioProfitRate(portfolioProfitDto.getPortfolioProfitRate())
                .build();
    }
}
