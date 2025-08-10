package com.acoldbottle.stockmate.api.sse.mock;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.sse.watchlist.WatchlistSseService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class WatchlistMock {

    private final WatchlistSseService watchlistSseService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private final List<String> symbols = List.of("AAPL", "TSLA", "RKLB", "GOOGL", "CAKE");

    @PostConstruct
    public void startMock() {
        scheduler.scheduleAtFixedRate(this::notifyRandomPrice, 0, 10, TimeUnit.SECONDS);
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

            watchlistSseService.notifyUpdatedWatchItem(symbol, priceDTO);
        }
    }
}
