package com.acoldbottle.stockmate.event.currentprice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestCurrentPriceEvent {

    private final String symbol;
    private final String marketCode;
}
