package com.acoldbottle.stockmate.event.currentprice;

import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceService;
import com.acoldbottle.stockmate.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestCurrentPriceEventListener {

    private final EmailService emailService;
    private final CurrentPriceService currentPriceService;

    @EventListener
    public void handleRequestCurrentPriceEvent(RequestCurrentPriceEvent event) {
        String symbol = event.getSymbol();
        String marketCode = event.getMarketCode();
        CompletableFuture.runAsync(() -> currentPriceService.requestAndUpdateCurrentPrice(symbol, marketCode))
                .exceptionally(e -> {
                    log.error("[TrackedSymbolService] 저장 중에 오류 발생, symbol={}", symbol, e);
                    emailService.sendEmailAlertError("[" + symbol + "] -> " + e.getCause().getMessage());
                    return null;
                });
    }
}
