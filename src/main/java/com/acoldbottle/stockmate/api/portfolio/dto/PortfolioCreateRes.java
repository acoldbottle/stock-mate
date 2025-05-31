package com.acoldbottle.stockmate.api.portfolio.dto;

import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioCreateRes {

    private Long portfolioId;
    private String title;

    public static PortfolioCreateRes from(Portfolio portfolio) {
        return PortfolioCreateRes.builder()
                .portfolioId(portfolio.getId())
                .title(portfolio.getTitle())
                .build();
    }
}
