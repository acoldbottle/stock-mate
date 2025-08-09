package com.acoldbottle.stockmate.api.trackedsymbol.service;

import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceService;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbolRepository;
import com.acoldbottle.stockmate.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final CurrentPriceService currentPriceService;
    private final EmailService emailService;

    @Transactional
    public void saveTrackedSymbolIfNotExists(String symbol, String marketCode) {
        TrackedSymbol trackedSymbol = TrackedSymbol.builder()
                .symbol(symbol)
                .marketCode(marketCode)
                .build();

        if (!trackedSymbolRepository.existsById(symbol)) {
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                trackedSymbolRepository.save(trackedSymbol);
                CompletableFuture<Void> future = currentPriceService.requestAndUpdateCurrentPrice(symbol, marketCode, executor)
                        .exceptionally(e -> {
                            emailService.sendEmailAlertError("[" + symbol + "] -> " + e);
                            return null;
                        });

                future.join();
            } catch (DataIntegrityViolationException e) {
                log.info("[TrackedSymbolService] 이미 존재하는 주식 종목");
            } catch (Exception e) {
                log.error("[TrackedSymbolService] 저장 중에 오류 발생, symbol={}", symbol, e);
                emailService.sendEmailAlertError("[" + symbol + "] -> " + e.getCause().getMessage());
            }
        }
    }

    @Transactional
    public void deleteTrackedSymbolIfNotUse(Stock stock) {
        trackedSymbolRepository.deleteTrackedSymbolIfNotExists(stock.getSymbol());
    }

    public List<TrackedSymbol> getTrackedSymbolAll() {
        return trackedSymbolRepository.findAll();
    }
}
