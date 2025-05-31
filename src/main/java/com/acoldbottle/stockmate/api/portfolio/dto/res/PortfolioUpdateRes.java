package com.acoldbottle.stockmate.api.portfolio.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioUpdateRes {

    private Long portfolioId;
    private String title;

    public static PortfolioUpdateRes from(Long portfolioId, String newTitle) {
        return PortfolioUpdateRes.builder()
                .portfolioId(portfolioId)
                .title(newTitle)
                .build();
    }
}
