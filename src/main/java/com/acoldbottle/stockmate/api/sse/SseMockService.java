package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.sse.holding.HoldingSseService;
import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioSseService;
import com.acoldbottle.stockmate.api.sse.watchlist.WatchlistSseService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.plaf.SpinnerUI;
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

    private final PortfolioSseService portfolioSseService;
    private final WatchlistSseService watchlistSseService;
    private final HoldingSseService holdingSseService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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

            CurrentPriceDTO priceDTO = CurrentPriceDTO.builder()
                    .last(price)
                    .rate(rate)
                    .build();

            log.info("[SseMockService] symbol={} --> price={}, rate={}",symbol, price, rate);
            holdingSseService.notifyUpdateHolding(symbol, priceDTO);
            portfolioSseService.notifyUpdatePortfolio(symbol);
            watchlistSseService.notifyUpdatedWatchItem(symbol, priceDTO);
        }
    }
}
