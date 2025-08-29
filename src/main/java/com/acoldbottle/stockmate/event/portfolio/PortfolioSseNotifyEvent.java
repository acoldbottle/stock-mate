package com.acoldbottle.stockmate.event.portfolio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PortfolioSseNotifyEvent {

    private final String symbol;
}
