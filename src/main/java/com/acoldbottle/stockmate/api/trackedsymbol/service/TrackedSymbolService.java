package com.acoldbottle.stockmate.api.trackedsymbol.service;

import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbol;
import com.acoldbottle.stockmate.domain.trackedsymbol.TrackedSymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackedSymbolService {

    private final TrackedSymbolRepository trackedSymbolRepository;


    @Transactional
    public void saveTrackedSymbolIfNotExists(String symbol, String marketCode) {
        TrackedSymbol trackedSymbol = TrackedSymbol.builder()
                .symbol(symbol)
                .marketCode(marketCode)
                .build();

        try {
            if (!trackedSymbolRepository.existsById(symbol)){
                trackedSymbolRepository.save(trackedSymbol);
            }
        } catch (DataIntegrityViolationException e) {
            log.info("[TrackedSymbolService] 이미 존재하는 주식 종목");
        } catch (Exception e) {
            log.error("[TrackedSymbolService] 저장 중에 오류 발생, symbol={}", symbol, e);
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
