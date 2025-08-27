package com.acoldbottle.stockmate.event.holding;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.sse.holding.HoldingSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HoldingSseNotifyEventListener {

    private final HoldingSseService holdingSseService;

    @EventListener
    public void handlePriceUpdate(HoldingSseNotifyEvent event) {
        String symbol = event.getSymbol();
        CurrentPriceDTO currentPriceDTO = event.getCurrentPriceDTO();

        holdingSseService.notifyUpdateHolding(symbol, currentPriceDTO);
    }
}
