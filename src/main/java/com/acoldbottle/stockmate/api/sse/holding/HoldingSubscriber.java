package com.acoldbottle.stockmate.api.sse.holding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HoldingSubscriber {

    private Long userId;

    private Long portfolioId;
}
