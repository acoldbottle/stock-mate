package com.acoldbottle.stockmate.api.portfolio.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PortfolioUpdateReq {

    @NotBlank
    private String title;
}
