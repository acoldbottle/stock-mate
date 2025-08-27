package com.acoldbottle.stockmate.event.holding;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HoldingSseNotifyEvent {

    private final String symbol;
    private final CurrentPriceDTO currentPriceDTO;
}
