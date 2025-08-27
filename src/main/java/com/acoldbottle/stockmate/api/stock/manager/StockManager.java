package com.acoldbottle.stockmate.api.stock.manager;

import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.stock.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockManager {

    private final StockRepository stockRepository;

    public Stock get(String symbol) {
        return stockRepository.findById(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorCode.STOCK_NOT_FOUND));
    }

    public List<Stock> search(String keyword) {
        return stockRepository.searchByKeyword(keyword);
    }

    public void updateDelistedStock(List<Stock> stockList) {
        Set<String> updatedSymbolSet = stockList.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.toSet());

        stockRepository.findAll().stream()
                .filter(existStock -> !updatedSymbolSet.contains(existStock.getSymbol()))
                .forEach(Stock::delistStock);
    }

    public void updateStocks(List<Stock> stockList) {
        stockRepository.saveAll(stockList);
    }
}
