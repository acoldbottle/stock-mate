package com.acoldbottle.stockmate.api.stock.service;

import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.external.kis.stockfile.StockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    public List<StockSearchRes> searchByKeyword(String keyword) {
        List<Stock> searchedList = stockRepository.searchByKeyword(keyword);
        return searchedList.stream()
                .map(StockSearchRes::from)
                .limit(10)
                .toList();
    }

    @Transactional
    public void saveStocks(List<StockDTO> stockDTOS) {
        List<Stock> stockListNotDelist = stockDTOS.stream()
                .map(stockDTO -> Stock.builder()
                        .symbol(stockDTO.getSymbol())
                        .marketCode(stockDTO.getMarketCode())
                        .korName(stockDTO.getKorName())
                        .engName(stockDTO.getEngName())
                        .build())
                .toList();

        Set<String> symbolSetNotDelist = stockListNotDelist.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.toSet());

        stockRepository.findAll().stream()
                .filter(stock -> !symbolSetNotDelist.contains(stock.getSymbol()))
                .forEach(Stock::delistStock);

        stockRepository.saveAll(stockListNotDelist);
    }
}
