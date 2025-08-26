package com.acoldbottle.stockmate.api.sse.watchlist;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WatchlistNotifyEventListener {

    private final WatchlistSseService watchlistSseService;

    @EventListener
    public void handlePriceUpdate(WatchlistSseNotifyEvent event){
        String symbol = event.getSymbol();
        CurrentPriceDTO currentPriceDTO = event.getCurrentPriceDTO();
        WatchItemUpdateDto updateDto = WatchItemUpdateDto.from(symbol, currentPriceDTO);

        watchlistSseService.notifyUpdatedWatchItem(updateDto);
    }
}
