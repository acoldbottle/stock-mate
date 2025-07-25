package com.acoldbottle.stockmate.api.watchlist.dto.res;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WatchItemGetRes {

    private Long watchItemId;
    private String symbol;
    private String korName;
    private String engName;
    private String marketCode;
    private BigDecimal currentPrice;
    private BigDecimal rate;

    public static WatchItemGetRes from(WatchItem watchItem, CurrentPriceDTO currentPriceDTO) {
        if (currentPriceDTO == null) {
            return WatchItemGetRes.builder()
                    .watchItemId(watchItem.getId())
                    .symbol(watchItem.getStock().getSymbol())
                    .korName(watchItem.getStock().getKorName())
                    .engName(watchItem.getStock().getEngName())
                    .marketCode(watchItem.getStock().getMarketCode())
                    .currentPrice(BigDecimal.ZERO)
                    .rate(BigDecimal.ZERO)
                    .build();
        }
        return WatchItemGetRes.builder()
                .watchItemId(watchItem.getId())
                .symbol(watchItem.getStock().getSymbol())
                .korName(watchItem.getStock().getKorName())
                .engName(watchItem.getStock().getEngName())
                .marketCode(watchItem.getStock().getMarketCode())
                .currentPrice(currentPriceDTO.getLast())
                .rate(currentPriceDTO.getRate())
                .build();
    }
}
