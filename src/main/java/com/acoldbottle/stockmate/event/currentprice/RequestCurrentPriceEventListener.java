package com.acoldbottle.stockmate.event.currentprice;

import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class RequestCurrentPriceEventListener {

    private final CurrentPriceService currentPriceService;

    @Async
    @EventListener
    public void handleRequestCurrentPriceEvent(RequestCurrentPriceEvent event) {
        String symbol = event.getSymbol();
        String marketCode = event.getMarketCode();
        currentPriceService.requestAndUpdateStock(symbol, marketCode);
    }
}
