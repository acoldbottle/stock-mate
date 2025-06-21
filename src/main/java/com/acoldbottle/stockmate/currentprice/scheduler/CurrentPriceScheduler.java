package com.acoldbottle.stockmate.currentprice.scheduler;

import com.acoldbottle.stockmate.api.trackedsymbol.service.TrackedSymbolService;
import com.acoldbottle.stockmate.currentprice.service.CurrentPriceService;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.external.kis.KisCurrentPriceRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceScheduler {

    private final TrackedSymbolService trackedSymbolService;
    private final CurrentPriceService currentPriceService;
    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();


    @Scheduled(fixedRate = 60000L)
    public void requestCurrentPriceAndCache() {
        try {
            List<TrackedSymbol> trackedSymbolList = trackedSymbolService.getTrackedSymbolAll();
            for (TrackedSymbol t : trackedSymbolList) {
                KisCurrentPriceRes currentPriceRes = currentPriceService.requestCurrentPriceToKisAPI(t.getSymbol(), t.getMarketCode(), executor);
                currentPriceService.updateCurrentPrice(t.getSymbol(), currentPriceRes, executor);
            }
        } catch (InterruptedException e) {
            log.error("=== [스케줄러] 인터럽트 발생 ===");
        } catch (Exception e) {
            log.error("=== [스케줄러] 현재가 요청 실패 ===");
        }
    }
}
