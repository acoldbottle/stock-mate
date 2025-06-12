package com.acoldbottle.stockmate.api.trackedsymbol.service;

import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackedSymbolService {

    private final TrackedSymbolRepository trackedSymbolRepository;
    private final HoldingRepository holdingRepository;

    @Async("virtualExecutor")
    @Transactional
    public void saveSymbolIfNotExists(String symbol, String marketCode) {
        try {
            trackedSymbolRepository.save(TrackedSymbol.builder()
                    .symbol(symbol)
                    .marketCode(marketCode)
                    .build());
        } catch (DataIntegrityViolationException e) {
            log.debug("해당 심볼 이미 존재 : {}", symbol);
        }
    }

    @Transactional
    public void deleteSymbolIfNotUse(Stock stock) {
        log.info("=== stock.symbol = {} ===", stock.getSymbol());
        if (!holdingRepository.existsByStock_symbol(stock.getSymbol())) {
            trackedSymbolRepository.deleteById(stock.getSymbol());
        }
    }
}
