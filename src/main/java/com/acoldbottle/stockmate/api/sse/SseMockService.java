package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.event.holding.HoldingSseNotifyEvent;
import com.acoldbottle.stockmate.event.portfolio.PortfolioSseNotifyEvent;
import com.acoldbottle.stockmate.event.watchlist.WatchlistSseNotifyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseMockService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ApplicationEventPublisher eventPublisher;
    private final Random random = new Random();
    private final List<String> symbols = List.of("AAPL", "TSLA", "NVDA");

//    @PostConstruct
    public void startMock() {
        scheduler.scheduleAtFixedRate(this::notifyRandomPrice, 0, 3, TimeUnit.SECONDS);
    }

    private void notifyRandomPrice() {
        for (String symbol : symbols) {
            BigDecimal price = BigDecimal.valueOf(5 + random.nextDouble() * 500)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal rate = BigDecimal.valueOf(random.nextDouble() * 10 - 5)
                    .setScale(2, RoundingMode.HALF_UP);

            CurrentPriceDTO currentPriceDto = CurrentPriceDTO.builder()
                    .last(price)
                    .rate(rate)
                    .build();

            log.info("[SseMockService] symbol={} --> price={}, rate={}",symbol, price, rate);

            eventPublisher.publishEvent(new PortfolioSseNotifyEvent(symbol));
            eventPublisher.publishEvent(new HoldingSseNotifyEvent(symbol, currentPriceDto));
            eventPublisher.publishEvent(new WatchlistSseNotifyEvent(symbol, currentPriceDto));
        }
    }
}
