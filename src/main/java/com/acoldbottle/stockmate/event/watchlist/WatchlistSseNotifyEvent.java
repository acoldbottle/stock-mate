package com.acoldbottle.stockmate.event.watchlist;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WatchlistSseNotifyEvent {

    private final String symbol;
    private final CurrentPriceDTO currentPriceDTO;
}
