package com.acoldbottle.stockmate.scheduler;

import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceService;
import com.acoldbottle.stockmate.api.trackedsymbol.service.TrackedSymbolService;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.email.EmailService;
import com.acoldbottle.stockmate.exception.kis.KisRequestInterruptedException;
import com.acoldbottle.stockmate.exception.kis.KisTooManyRequestException;
import com.acoldbottle.stockmate.exception.kis.KisUpdateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceScheduler {

    private final TrackedSymbolService trackedSymbolService;
    private final CurrentPriceService currentPriceService;
    private final EmailService emailService;

    @Scheduled(fixedRate = 60000L)
    public void requestCurrentPriceAndCache() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Map<String, String> failedMap = new ConcurrentHashMap<>();

            List<TrackedSymbol> trackedSymbolList = trackedSymbolService.getTrackedSymbolAll();
            List<CompletableFuture<Void>> futures = trackedSymbolList.stream()
                    .map(t -> currentPriceService.requestAndUpdateCurrentPrice(t.getSymbol(), t.getMarketCode(), executor)
                            .exceptionally(e -> {
                                Throwable cause = e.getCause();
                                if (cause instanceof KisTooManyRequestException) {
                                    failedMap.put(t.getSymbol(), cause.getMessage());
                                } else if (cause instanceof KisUpdateException) {
                                    failedMap.put(t.getSymbol(), cause.getMessage());
                                } else if (cause instanceof KisRequestInterruptedException) {
                                    failedMap.put(t.getSymbol(), cause.getMessage());
                                }
                                return null;
                            }))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            if (!failedMap.isEmpty()) {
                emailService.sendEmailAlertErrorByMap(failedMap);
            }
        } catch (Exception e) {
            log.error("=== [스케줄러] 현재가 업데이트 실패 ===", e);
            emailService.sendEmailAlertError("정의하지 않은 예외 발생 -> " + e.getMessage());
        }
    }
}
