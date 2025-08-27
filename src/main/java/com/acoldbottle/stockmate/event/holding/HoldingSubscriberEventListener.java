package com.acoldbottle.stockmate.event.holding;

import com.acoldbottle.stockmate.api.sse.holding.HoldingSubscriberRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.acoldbottle.stockmate.event.holding.HoldingSubscriberEvent.*;

@Component
@RequiredArgsConstructor
public class HoldingSubscriberEventListener {

    private final HoldingSubscriberRegistry subscriberRegistry;

    @EventListener
    public void handlerSubscriberEvent(HoldingSubscriberEvent subscriberEvent) {
        Long userId = subscriberEvent.getUserId();
        Long portfolioId = subscriberEvent.getPortfolioId();
        String symbol = subscriberEvent.getSymbol();
        HoldingEventType eventType = subscriberEvent.getEventType();

        switch (eventType) {
            case CREATE: subscriberRegistry.register(userId, portfolioId, symbol); break;
            case DELETE: subscriberRegistry.unregister(userId, portfolioId, symbol); break;
        }
    }
}
