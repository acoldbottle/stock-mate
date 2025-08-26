package com.acoldbottle.stockmate.api.trackedsymbol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestCurrentPriceEvent {

    private final String symbol;
    private final String marketCode;
}
