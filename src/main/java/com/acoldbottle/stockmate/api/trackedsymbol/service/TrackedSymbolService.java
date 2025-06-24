package com.acoldbottle.stockmate.api.trackedsymbol.service;

import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbolRepository;
import com.acoldbottle.stockmate.domain.watchitem.WatchItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackedSymbolService {

    private final TrackedSymbolRepository trackedSymbolRepository;
    private final HoldingRepository holdingRepository;
    private final WatchItemRepository watchItemRepository;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public CompletableFuture<Void> saveTrackedSymbolIfNotExists(String symbol, String marketCode) {
        return CompletableFuture.runAsync(() ->
                        trackedSymbolRepository.save(TrackedSymbol.builder()
                                .symbol(symbol)
                                .marketCode(marketCode)
                                .build()), executor
                )
                .exceptionally(ex -> {
                    log.debug("해당 심볼 이미 존재 : {}", symbol);
                    return null;
                });
    }

    @Transactional
    public void deleteTrackedSymbolIfNotUse(Stock stock) {
        log.info("=== stock.symbol = {} ===", stock.getSymbol());
        if (!holdingRepository.existsByStock_symbol(stock.getSymbol())) {
            trackedSymbolRepository.deleteById(stock.getSymbol());
        }
    }

    public List<TrackedSymbol> getTrackedSymbolAll() {
        return trackedSymbolRepository.findAll();
    }
}
