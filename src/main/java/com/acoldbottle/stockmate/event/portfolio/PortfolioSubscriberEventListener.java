package com.acoldbottle.stockmate.event.portfolio;

import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioSubscriberRegistry;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PortfolioSubscriberEventListener {

    private final PortfolioSubscriberRegistry portfolioSubscriberRegistry;

    @EventListener
    public void handlerSubscriberUnregisterEvent(PortfolioSubscriberEvent subscriberEvent) {
        Portfolio portfolio = subscriberEvent.getPortfolio();
        List<Holding> holdingList = subscriberEvent.getHoldingList();
        List<String> symbolList = holdingList.stream()
                .map(holding -> holding.getStock().getSymbol())
                .toList();

        portfolioSubscriberRegistry.unregister(portfolio.getId(), symbolList);
    }
}
