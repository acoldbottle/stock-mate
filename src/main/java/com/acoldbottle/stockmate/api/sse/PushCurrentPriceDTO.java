package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PushCurrentPriceDTO {

    private String symbol;
    private BigDecimal price;
    private BigDecimal rate;

    public static PushCurrentPriceDTO from(String symbol, CurrentPriceDTO currentPriceDTO) {
        return PushCurrentPriceDTO.builder()
                .symbol(symbol)
                .price(currentPriceDTO.getLast())
                .rate(currentPriceDTO.getRate())
                .build();
    }
}
