package com.acoldbottle.stockmate.api.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PortfolioCreateReq {

    @NotBlank
    private String title;
}
