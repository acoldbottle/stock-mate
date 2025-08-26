package com.acoldbottle.stockmate.api.sse.holding;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.holding.service.HoldingManager;
import com.acoldbottle.stockmate.domain.holding.Holding;
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
public class HoldingSseService {

    private final HoldingEmitterRegistry emitterRegistry;
    private final HoldingSubscriberRegistry subscriberRegistry;
    private final HoldingManager holdingManager;

    public SseEmitter connect(Long userId, Long portfolioId) {
        Optional<SseEmitter> isExistEmitter = emitterRegistry.getEmitterByUserId(userId);
        if (isExistEmitter.isPresent()) {
            if (subscriberRegistry.isRegisteredPortfolioId(portfolioId)){
                return isExistEmitter.get();
            }
        }
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        emitterRegistry.register(userId, newEmitter);
        initializeUserSubscriptions(userId, portfolioId);
        log.info("[HoldingSseService] user={}, portfolioId={} --> connect", userId, portfolioId);

        newEmitter.onCompletion(() -> {
            emitterRegistry.unregister(userId);
            unregisterUserSubscriptions(userId, portfolioId);
            log.info("[HoldingSseService] user={}, portfolioId={} --> connection closed", userId, portfolioId);
        });
        newEmitter.onError((e) -> {
            emitterRegistry.unregister(userId);
            unregisterUserSubscriptions(userId, portfolioId);
            log.error("[HoldingSseService] user={}, portfolioId={} --> connection Error!!", userId, portfolioId, e);
        });
        return newEmitter;
    }

    public void disconnect(Long userId, Long portfolioId) {
        unregisterUserSubscriptions(userId, portfolioId);
        SseEmitter unregisteredEmitter = emitterRegistry.unregister(userId);
        if (unregisteredEmitter != null) {
            unregisteredEmitter.complete();
        }
    }

    public void notifyUpdateHolding(String symbol, CurrentPriceDTO currentPriceDTO) {
        Set<HoldingSubscriber> subscribers = subscriberRegistry.getSubscribersBySymbol(symbol);
        if (subscribers==null || subscribers.isEmpty()) return;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = subscribers.stream()
                    .map(s ->
                            CompletableFuture.runAsync(() ->
                                    emitterRegistry.getEmitterByUserId(s.getUserId())
                                            .ifPresent(emitter ->
                                                    sendToClient(emitter,
                                                            HoldingUpdateDto.from(
                                                                    s.getPortfolioId(),
                                                                    symbol,
                                                                    currentPriceDTO))), executor))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    private void initializeUserSubscriptions(Long userId, Long portfolioId) {
        List<Holding> holdingList = holdingManager.getHoldingList(portfolioId);
        subscriberRegistry.registerAll(userId, portfolioId, holdingList);
    }

    private void unregisterUserSubscriptions(Long userId, Long portfolioId) {
        List<Holding> holdingList = holdingManager.getHoldingList(portfolioId);
        subscriberRegistry.unregisterAll(userId, portfolioId, holdingList);
    }

    private void sendToClient(SseEmitter emitter, HoldingUpdateDto updateData) {
        try {
            emitter.send(SseEmitter.event()
                    .name("holding-price-update")
                    .data(updateData));
        } catch (IOException e) {
            emitter.completeWithError(e);
            log.error("[HoldingSseService] 오류 발생", e);
        }
    }
}
