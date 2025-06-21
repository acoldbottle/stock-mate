package com.acoldbottle.stockmate.api.stock.service;

import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.external.kis.stockfile.StockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    public List<StockSearchRes> searchByKeyword(String keyword) {
        List<Stock> searchedList = stockRepository.searchByKeyword(keyword);
        return searchedList.stream()
                .map(StockSearchRes::from)
                .toList();
    }

    @Transactional
    public void saveStocks(List<StockDTO> stockDTOS) {
        List<Stock> stockList = stockDTOS.stream()
                .map(stockDTO -> Stock.builder()
                        .symbol(stockDTO.getSymbol())
                        .korName(stockDTO.getKorName())
                        .engName(stockDTO.getEngName())
                        .marketCode(stockDTO.getMarketCode())
                        .build())
                .toList();

        stockRepository.saveAll(stockList);
    }
}
