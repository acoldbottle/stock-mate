package com.acoldbottle.stockmate.api.sse.holding;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class HoldingUpdateDto {

    private Long portfolioId;

    private String symbol;

    private BigDecimal price;

    private BigDecimal rate;

    public static HoldingUpdateDto from(Long portfolioId, String symbol, CurrentPriceDTO currentPriceDTO) {
        return HoldingUpdateDto.builder()
                .symbol(symbol)
                .price(currentPriceDTO.getLast())
                .rate(currentPriceDTO.getRate())
                .build();
    }
}
