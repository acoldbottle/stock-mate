package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentPricePushService {

    private final CurrentPriceCacheService cacheService;
    private final EmitterRegistry emitterRegistry;
    private final SubscriberRegistry subscriberRegistry;

    public void pushUpdatedPrice(String symbol) {
        Set<Long> userIds = subscriberRegistry.findSubscribersBySymbol(symbol);
        CurrentPriceDTO currentPrice = cacheService.getCurrentPrice(symbol);
        PushCurrentPriceDTO pushDTO = PushCurrentPriceDTO.from(symbol, currentPrice);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = userIds.stream()
                    .map(userId -> CompletableFuture.runAsync(() -> {
                        emitterRegistry.findByUserId(userId).ifPresent(emitter -> {
                            sendEvent(emitter, pushDTO);
                        });
                    }, executor))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    private void sendEvent(SseEmitter emitter, PushCurrentPriceDTO pushDTO) {
        try {
            emitter.send(SseEmitter.event()
                    .name("price-update")
                    .data(pushDTO));
        } catch (IOException e) {
            emitter.completeWithError(e);
            log.error("[CurrentPricePushService] 오류 발생", e);
        }
    }
}
