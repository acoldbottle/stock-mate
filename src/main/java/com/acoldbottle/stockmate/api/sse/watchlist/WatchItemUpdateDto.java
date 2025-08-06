package com.acoldbottle.stockmate.api.sse.watchlist;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WatchItemUpdateDto {

    private String symbol;

    private BigDecimal price;

    private BigDecimal rate;

    public static WatchItemUpdateDto from(String symbol, CurrentPriceDTO currentPriceDTO) {
        return WatchItemUpdateDto.builder()
                .symbol(symbol)
                .price(currentPriceDTO.getLast())
                .rate(currentPriceDTO.getRate())
                .build();
    }
}
