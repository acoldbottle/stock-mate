package com.acoldbottle.stockmate.api.sse.portfolio;

import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import com.acoldbottle.stockmate.api.profit.service.ProfitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioSseService {

    private final PortfolioEmitterRegistry emitterRegistry;
    private final PortfolioSubscriberRegistry subscriberRegistry;
    private final ProfitService profitService;

    public SseEmitter connect(Long userId) {
        Optional<SseEmitter> isExistEmitter = emitterRegistry.getEmitterByUserId(userId);
        if (isExistEmitter.isPresent()) {
            return isExistEmitter.get();
        }
        SseEmitter newEmitter = new SseEmitter(Long.MAX_VALUE);
        emitterRegistry.register(userId, newEmitter);
        subscriberRegistry.registerAllByUserId(userId);
        log.info("[PortfolioSseService] user={} --> connect", userId);
        newEmitter.onCompletion(() -> {
            emitterRegistry.unregister(userId);
            subscriberRegistry.unregisterByUserId(userId);
            log.info("[PortfolioSseService] user={} --> connection closed", userId);
        });
        newEmitter.onError((e) -> {
            emitterRegistry.unregister(userId);
            subscriberRegistry.unregisterByUserId(userId);
            log.error("[PortfolioSseService] user={} --> connection Error!!", userId, e);
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

    public void notifyUpdatePortfolio(String symbol) {
        Set<Long> subscribers = subscriberRegistry.getSubscribersBySymbol(symbol);
        subscribers.stream()
                .map(subscriberRegistry::getHoldingsBySubscriber)
                .forEach(holdings -> {
                    Long portfolioId = holdings.getFirst().getPortfolio().getId();
                    Long userId = holdings.getFirst().getPortfolio().getUser().getId();
                    ProfitDTO profitDTO = profitService.calculateProfitInPortfolio(holdings);
                    PortfolioUpdateDto updateData = PortfolioUpdateDto.from(portfolioId, profitDTO);
                    emitterRegistry.getEmitterByUserId(userId).ifPresent(emitter -> sendEvent(emitter, updateData));
                });
    }

    private void sendEvent(SseEmitter emitter, PortfolioUpdateDto updateData) {
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
