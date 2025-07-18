package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class PortfolioHoldingListDTO {
    private String portfolioTitle;
    private BigDecimal portfolioCurrentValue;
    private BigDecimal portfolioProfitAmount;
    private BigDecimal portfolioProfitRate;
    private List<ProfitDTO.HoldingProfitDTO> holdingList;

    public static PortfolioHoldingListDTO from(String title, ProfitDTO profitDTO) {
        return PortfolioHoldingListDTO.builder()
                .portfolioTitle(title)
                .portfolioCurrentValue(profitDTO.getPortfolioCurrentValue())
                .portfolioProfitAmount(profitDTO.getPortfolioProfitAmount())
                .portfolioProfitRate(profitDTO.getPortfolioProfitRate())
                .holdingList(profitDTO.getHoldingList())
                .build();
    }
}
