package com.acoldbottle.stockmate.api.profit.dto;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.domain.holding.Holding;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class HoldingCurrentInfoDto {

    private Long portfolioId;
    private Long holdingId;
    private BigDecimal avgPurchasePrice;
    private int quantity;
    private BigDecimal currentPrice;
    private BigDecimal rate;

    public static HoldingCurrentInfoDto from(Holding holding, CurrentPriceDTO priceDTO) {
        if (priceDTO == null) {
            return HoldingCurrentInfoDto.builder()
                    .portfolioId(holding.getPortfolio().getId())
                    .holdingId(holding.getId())
                    .avgPurchasePrice(holding.getPurchasePrice())
                    .quantity(holding.getQuantity())
                    .currentPrice(BigDecimal.ZERO)
                    .rate(BigDecimal.ZERO)
                    .build();
        }
        return HoldingCurrentInfoDto.builder()
                .portfolioId(holding.getPortfolio().getId())
                .holdingId(holding.getId())
                .avgPurchasePrice(holding.getPurchasePrice())
                .quantity(holding.getQuantity())
                .currentPrice(priceDTO.getLast())
                .rate(priceDTO.getRate())
                .build();
    }
}
