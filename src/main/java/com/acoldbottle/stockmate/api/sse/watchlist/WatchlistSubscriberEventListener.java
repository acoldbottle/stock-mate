package com.acoldbottle.stockmate.api.sse.watchlist;

import com.acoldbottle.stockmate.api.sse.watchlist.WatchlistSubscriberEvent.WatchlistEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WatchlistSubscriberEventListener {

    private final WatchlistSubscriberRegistry subscriberRegistry;

    @EventListener
    public void handlerSubscriberEvent(WatchlistSubscriberEvent subscriberEvent) {
        Long userId = subscriberEvent.getUserId();
        String symbol = subscriberEvent.getSymbol();
        WatchlistEventType eventType = subscriberEvent.getEventType();

        switch (eventType) {
            case CREATE: subscriberRegistry.register(userId, symbol); break;
            case DELETE: subscriberRegistry.unregister(userId, symbol); break;
        }
    }
}
