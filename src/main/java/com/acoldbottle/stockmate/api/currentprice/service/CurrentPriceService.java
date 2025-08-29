package com.acoldbottle.stockmate.api.currentprice.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.trackedsymbol.manager.TrackedSymbolManager;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.event.email.EmailAlertByMapEvent;
import com.acoldbottle.stockmate.event.email.EmailAlertEvent;
import com.acoldbottle.stockmate.event.holding.HoldingSseNotifyEvent;
import com.acoldbottle.stockmate.event.portfolio.PortfolioSseNotifyEvent;
import com.acoldbottle.stockmate.event.watchlist.WatchlistSseNotifyEvent;
import com.acoldbottle.stockmate.exception.kis.KisRequestInterruptedException;
import com.acoldbottle.stockmate.exception.kis.KisUpdateException;
import com.acoldbottle.stockmate.external.kis.KisAPIClient;
import com.acoldbottle.stockmate.external.kis.KisCurrentPriceRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.acoldbottle.stockmate.exception.ErrorCode.KIS_REQUEST_INTERRUPTED_ERROR;
import static com.acoldbottle.stockmate.exception.ErrorCode.KIS_UPDATE_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceService {

    private final KisAPIClient kisAPIClient;
    private final CurrentPriceCacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;
    private final TrackedSymbolManager trackedSymbolManager;


    public void requestAndUpdateStocks() {
        List<TrackedSymbol> stocks = trackedSymbolManager.getTrackedSymbolAll();
        if (stocks.isEmpty()) return;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Map<String, String> failedMap = new ConcurrentHashMap<>();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            stocks.forEach(s -> {
                        CompletableFuture<Void> future = CompletableFuture
                                        .supplyAsync(() -> requestCurrentPrice(s.getSymbol(), s.getMarketCode()), executor)
                                        .thenAccept(kisCurrentPriceRes -> updateCurrentPrice(kisCurrentPriceRes, s.getSymbol()))
                                        .exceptionally(e -> {
                                            Throwable cause = e.getCause() != null ? e.getCause() : e;
                                            failedMap.put(s.getSymbol(), cause.getMessage());
                                            return null;
                                        });
                        futures.add(future);
                            }
                    );
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            if (!failedMap.isEmpty()) {
                eventPublisher.publishEvent(new EmailAlertByMapEvent(failedMap));
            }
        }
    }

    public void requestAndUpdateStock(String symbol, String marketCode) {
        try {
            KisCurrentPriceRes res = requestCurrentPrice(symbol, marketCode);
            updateCurrentPrice(res, symbol);
        } catch (Exception e) {
            eventPublisher.publishEvent(new EmailAlertEvent(e.getMessage()));
        }
    }

    private KisCurrentPriceRes requestCurrentPrice(String symbol, String marketCode) {
        try {
            return kisAPIClient.requestCurrentPrice(symbol, marketCode);
        } catch (InterruptedException e) {
            log.error("=== [CurrentPriceService] 현재가 업데이트 중 인터럽트 발생 ===");
            throw new KisRequestInterruptedException(KIS_REQUEST_INTERRUPTED_ERROR);
        }
    }

    private void updateCurrentPrice(KisCurrentPriceRes kisCurrentPriceRes, String symbol) {
        if (kisCurrentPriceRes == null) {
            log.warn("kisCurrentPriceRes == null, 현재가 업데이트 생략 -> symbol={}", symbol);
            throw new KisUpdateException(KIS_UPDATE_ERROR);
        }
        CurrentPriceDTO currentPriceDto = CurrentPriceDTO.from(kisCurrentPriceRes);
        boolean isUpdated = cacheService.updateCurrentPrice(symbol, currentPriceDto);
        if (isUpdated) {
            eventPublisher.publishEvent(new PortfolioSseNotifyEvent(symbol));
            eventPublisher.publishEvent(new HoldingSseNotifyEvent(symbol, currentPriceDto));
            eventPublisher.publishEvent(new WatchlistSseNotifyEvent(symbol, currentPriceDto));
        }
    }
}
