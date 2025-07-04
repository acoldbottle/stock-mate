package com.acoldbottle.stockmate.scheduler;

import com.acoldbottle.stockmate.api.trackedsymbol.service.TrackedSymbolService;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceService;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceScheduler {

    private final TrackedSymbolService trackedSymbolService;
    private final CurrentPriceService currentPriceService;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(fixedRate = 60000L)
    public void requestCurrentPriceAndCache() {
        try {
            List<TrackedSymbol> trackedSymbolList = trackedSymbolService.getTrackedSymbolAll();

            List<CompletableFuture<Void>> futures = trackedSymbolList.stream()
                    .map(t -> currentPriceService.requestCurrentPriceToKisAPI(t.getSymbol(), t.getMarketCode(), executor)
                            .thenCompose(kisCurrentPriceRes -> currentPriceService.updateCurrentPrice(t.getSymbol(), kisCurrentPriceRes, executor))
                            .exceptionally(e -> {
                                log.error("=== 현재가 요청 or 업데이트 실패 ===", e);
                                return null;
                            }))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("=== [스케줄러] 현재가 업데이트 실패 ===", e);
        }
    }
}
