package com.acoldbottle.stockmate.api.sse.portfolio;

import com.acoldbottle.stockmate.api.holding.manager.HoldingManager;
import com.acoldbottle.stockmate.api.portfolio.manager.PortfolioManager;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioSseService {

    private final PortfolioEmitterRegistry emitterRegistry;
    private final PortfolioSubscriberRegistry subscriberRegistry;
    private final PortfolioManager portfolioManager;
    private final HoldingManager holdingManager;

    public SseEmitter connect(Long userId) {
        Optional<SseEmitter> isExistEmitter = emitterRegistry.getEmitterByUserId(userId);
        if (isExistEmitter.isPresent()) {
            return isExistEmitter.get();
        }

        SseEmitter newEmitter = new SseEmitter(2 * 60 * 60 * 1000L);
        emitterRegistry.register(userId, newEmitter);
        initializeUserSubscriptions(userId);
        log.info("[PortfolioSseService] user={} --> connect", userId);

        newEmitter.onCompletion(() -> {
            emitterRegistry.unregister(userId);
            unregisterUserSubscriptions(userId);
            log.info("[PortfolioSseService] user={} --> connection closed", userId);
        });
        newEmitter.onError((e) -> {
            emitterRegistry.unregister(userId);
            unregisterUserSubscriptions(userId);
            log.error("[PortfolioSseService] user={} --> connection Error!!", userId, e);
        });
        return newEmitter;
    }

    public void disconnect(Long userId) {
        SseEmitter unregisteredEmitter = emitterRegistry.unregister(userId);
        if (unregisteredEmitter != null) {
            unregisteredEmitter.complete();
        }
        unregisterUserSubscriptions(userId);
    }

    public void notifyUpdatePortfolio(Map<Long, List<PortfolioUpdateDto>> results) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = results.entrySet().stream()
                    .map(result -> CompletableFuture.runAsync(() -> {
                        Long userId = result.getKey();
                        List<PortfolioUpdateDto> updateDtoList = result.getValue();
                        updateDtoList.forEach(updateDto -> {
                            emitterRegistry.getEmitterByUserId(userId)
                                    .ifPresent(emitter -> sendToClient(emitter, updateDto));
                        });
                    }, executor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

    }

    private void initializeUserSubscriptions(Long userId) {
        List<Holding> holdingsByUserId = holdingManager.getHoldingListByUserId(userId);
        subscriberRegistry.registerAll(holdingsByUserId);
    }

    private void unregisterUserSubscriptions(Long userId) {
        List<Portfolio> portfolios = portfolioManager.getPortfolioList(userId);
        subscriberRegistry.unregisterAll(portfolios);
    }

    private void sendToClient(SseEmitter emitter, PortfolioUpdateDto updateData) {
        try {
            emitter.send(SseEmitter.event()
                    .name("portfolio-price-update")
                    .data(updateData));
        } catch (IOException e) {
            emitter.completeWithError(e);
            log.error("[PortfolioSseService] 오류 발생", e);
        }
    }
}
