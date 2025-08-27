package com.acoldbottle.stockmate.event.portfolio;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PortfolioSseNotifyEvent {

    private final String symbol;
    private final CurrentPriceDTO currentPriceDTO;
}
