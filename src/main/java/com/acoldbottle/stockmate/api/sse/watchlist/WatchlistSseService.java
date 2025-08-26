package com.acoldbottle.stockmate.api.sse.watchlist;

import com.acoldbottle.stockmate.api.watchlist.service.WatchItemManager;
import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
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
    private final WatchItemManager watchItemManager;

    public SseEmitter connect(Long userId) {
        Optional<SseEmitter> isExistEmitter = emitterRegistry.getEmitterByUserId(userId);
        if (isExistEmitter.isPresent()) {
            return isExistEmitter.get();
        }
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        emitterRegistry.register(userId, newEmitter);
        initializeUserSubscriptions(userId);
        log.info("[WatchlistSseService] user={} --> connect", userId);

        newEmitter.onCompletion(() -> {
            emitterRegistry.unregister(userId);
            unregisterUserSubscriptions(userId);
            log.info("[WatchlistSseService] user={} --> connection closed", userId);
        });
        newEmitter.onError((e) -> {
            emitterRegistry.unregister(userId);
            unregisterUserSubscriptions(userId);
            log.error("[WatchlistSseService] user={} --> connection Error!!", userId, e);
        });
        return newEmitter;
    }

    public void disconnect(Long userId) {
        unregisterUserSubscriptions(userId);
        SseEmitter unregisteredEmitter = emitterRegistry.unregister(userId);
        if (unregisteredEmitter != null) {
            unregisteredEmitter.complete();
        }
    }

    public void notifyUpdatedWatchItem(WatchItemUpdateDto updateDto) {
        Set<Long> subscribers = subscriberRegistry.getSubscribersBySymbol(updateDto.getSymbol());
        if (subscribers==null || subscribers.isEmpty()) return;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = subscribers.stream()
                    .map(userId ->
                            CompletableFuture.runAsync(() ->
                                    emitterRegistry.getEmitterByUserId(userId)
                                            .ifPresent(emitter -> sendEvent(emitter, updateDto)), executor)
                    )
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    private void initializeUserSubscriptions(Long userId) {
        List<WatchItem> watchlist = watchItemManager.getWatchlist(userId);
        subscriberRegistry.registerAll(userId, watchlist);
    }

    private void unregisterUserSubscriptions(Long userId) {
        List<WatchItem> watchlist = watchItemManager.getWatchlist(userId);
        subscriberRegistry.unregisterAll(userId, watchlist);
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
