package com.acoldbottle.stockmate.event.holding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HoldingSubscriberEvent {

    private final Long userId;
    private final Long portfolioId;
    private final String symbol;
    private final HoldingEventType eventType;

    public enum HoldingEventType {
        CREATE,
        DELETE
    }
}
