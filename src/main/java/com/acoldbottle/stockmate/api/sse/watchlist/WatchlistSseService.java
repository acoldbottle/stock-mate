package com.acoldbottle.stockmate.api.sse.watchlist;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
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
public class WatchlistSseService {

    private final WatchlistEmitterRegistry emitterRegistry;
    private final WatchlistSubscriberRegistry subscriberRegistry;
    public SseEmitter connect(Long userId) {
        Optional<SseEmitter> isExistEmitter = emitterRegistry.getEmitterByUserId(userId);
        if (isExistEmitter.isPresent()) {
            return isExistEmitter.get();
        }
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        emitterRegistry.register(userId, newEmitter);
        subscriberRegistry.registerAllByUserId(userId);
        log.info("[WatchlistSseService] user={} --> connect", userId);
        newEmitter.onCompletion(() -> {
            emitterRegistry.unregister(userId);
            subscriberRegistry.unregisterByUserId(userId);
            log.info("[WatchlistSseService] user={} --> connection closed", userId);
        });
        newEmitter.onError((e) -> {
            emitterRegistry.unregister(userId);
            subscriberRegistry.unregisterByUserId(userId);
            log.error("[WatchlistSseService] user={} --> connection Error!!", userId, e);
        });
        return newEmitter;
    }

    public void disconnect(Long userId) {
        subscriberRegistry.unregisterByUserId(userId);
        SseEmitter unregisteredEmitter = emitterRegistry.unregister(userId);
        if (unregisteredEmitter != null) {
            unregisteredEmitter.complete();
        }
    }

    public void notifyUpdatedWatchItem(String symbol, CurrentPriceDTO currentPriceDTO) {
        Set<Long> subscribers = subscriberRegistry.getSubscribersBySymbol(symbol);
        if (subscribers.isEmpty()) return;

        WatchItemUpdateDto updateData = WatchItemUpdateDto.from(symbol, currentPriceDTO);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()){
            List<CompletableFuture<Void>> futures = subscribers.stream()
                    .map(userId ->
                            CompletableFuture.runAsync(() ->
                            emitterRegistry.getEmitterByUserId(userId)
                                    .ifPresent(emitter -> sendEvent(emitter, updateData)), executor))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    private void sendEvent(SseEmitter emitter, WatchItemUpdateDto updateData) {
        try {
            emitter.send(SseEmitter.event()
                    .name("watchlist-price-update")
                    .data(updateData));
        } catch (IOException e) {
            emitter.completeWithError(e);
            log.error("[WatchlistSseService] 오류 발생", e);
        }
    }
}
