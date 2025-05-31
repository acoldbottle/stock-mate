package com.acoldbottle.stockmate.api.portfolio.dto.res;

import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioGetRes {

    private Long portfolioId;
    private String title;

    public static PortfolioGetRes from(Portfolio portfolio) {
        return PortfolioGetRes.builder()
                .portfolioId(portfolio.getId())
                .title(portfolio.getTitle())
                .build();
    }
}
