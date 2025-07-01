package com.acoldbottle.stockmate.api.trackedsymbol.service;

import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbolRepository;
import com.acoldbottle.stockmate.domain.watchitem.WatchItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackedSymbolService {

    private final TrackedSymbolRepository trackedSymbolRepository;
    private final HoldingRepository holdingRepository;
    private final WatchItemRepository watchItemRepository;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final TransactionTemplate transactionTemplate;

    public CompletableFuture<Void> saveTrackedSymbolIfNotExists(String symbol, String marketCode) {
        return CompletableFuture.runAsync(() -> saveTrackedSymbol(symbol, marketCode), executor)
                .exceptionallyComposeAsync(ex -> {
                    log.warn("[TrackedSymbolService] 저장 중에 오류 발생", ex);
                    log.warn("[TrackedSymbolService] 저장 재시도 -> symbol={}", symbol);
                    return CompletableFuture.runAsync(() -> saveTrackedSymbol(symbol, marketCode),
                                    CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS, executor))
                            .exceptionally(e -> {
                                log.warn("[TrackedSymbolService] 저장 실패");
                                return null;
                            });
                }, executor);
    }

    @Transactional
    public void deleteTrackedSymbolIfNotUse(Stock stock) {
        log.info("=== stock.symbol = {} ===", stock.getSymbol());
        if (!holdingRepository.existsByStock(stock) && !watchItemRepository.existsByStock(stock)) {
            trackedSymbolRepository.deleteById(stock.getSymbol());
        }
    }

    public List<TrackedSymbol> getTrackedSymbolAll() {
        return trackedSymbolRepository.findAll();
    }

    private void saveTrackedSymbol(String symbol, String marketCode) {
        transactionTemplate.executeWithoutResult(status -> {
            try {
                trackedSymbolRepository.save(TrackedSymbol.builder()
                        .symbol(symbol)
                        .marketCode(marketCode)
                        .build());
            } catch (DataIntegrityViolationException e) {
                log.debug("[TrackedSymbolService] 이미 존재하는 주식 -> symbol={}", symbol);
            }
        });
    }
}
