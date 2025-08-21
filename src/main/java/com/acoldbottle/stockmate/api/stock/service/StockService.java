package com.acoldbottle.stockmate.api.stock.service;

import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.external.kis.stockfile.StockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockManager stockManager;

    public List<StockSearchRes> searchByKeyword(String keyword) {
        List<Stock> searchResult = stockManager.search(keyword);
        return searchResult.stream()
                .map(StockSearchRes::from)
                .toList();
    }

    @Transactional
    public void updateStocks(List<StockDTO> stockDTOS) {
        List<Stock> stockList = stockDTOS.stream()
                .map(stockDTO -> Stock.builder()
                        .symbol(stockDTO.getSymbol())
                        .marketCode(stockDTO.getMarketCode())
                        .korName(stockDTO.getKorName())
                        .engName(stockDTO.getEngName())
                        .build())
                .toList();

        stockManager.updateDelistedStock(stockList);
        stockManager.updateStocks(stockList);
    }
}
