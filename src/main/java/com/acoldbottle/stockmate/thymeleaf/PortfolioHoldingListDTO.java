package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.api.holding.dto.res.HoldingWithProfitRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioWithProfitRes;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PortfolioHoldingListDTO {

    private PortfolioWithProfitRes portfolioInfo;
    private List<HoldingWithProfitRes> holdingList;
}
