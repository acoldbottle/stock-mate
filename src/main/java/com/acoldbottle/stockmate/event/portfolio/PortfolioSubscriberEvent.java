package com.acoldbottle.stockmate.event.portfolio;


import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PortfolioSubscriberEvent {

    private final Portfolio portfolio;

    private final List<Holding> holdingList;
}
