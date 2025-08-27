package com.acoldbottle.stockmate.api.trackedsymbol.manager;

import com.acoldbottle.stockmate.event.currentprice.RequestCurrentPriceEvent;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackedSymbolManager {

    private final TrackedSymbolRepository trackedSymbolRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void saveTrackedSymbolIfNotExists(String symbol, String marketCode) {
        if (trackedSymbolRepository.existsById(symbol)) {
            return;
        }
        try {
            trackedSymbolRepository.save(
                    TrackedSymbol.builder()
                            .symbol(symbol)
                            .marketCode(marketCode)
                            .build());
            eventPublisher.publishEvent(new RequestCurrentPriceEvent(symbol, marketCode));
        } catch (DataIntegrityViolationException e) {
            log.info("[TrackedSymbolService] 이미 존재하는 주식 종목");
        }
    }

    public void deleteTrackedSymbolIfNotUse(Stock stock) {
        trackedSymbolRepository.deleteTrackedSymbolIfNotExists(stock.getSymbol());
    }

    public List<TrackedSymbol> getTrackedSymbolAll() {
        return trackedSymbolRepository.findAll();
    }
}
