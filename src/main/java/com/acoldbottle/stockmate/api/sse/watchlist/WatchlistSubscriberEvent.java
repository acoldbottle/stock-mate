package com.acoldbottle.stockmate.api.sse.watchlist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WatchlistSubscriberEvent {

    private final Long userId;
    private final String symbol;
    private final WatchlistEventType eventType;

    public enum WatchlistEventType {
        CREATE,
        DELETE
    }
}
