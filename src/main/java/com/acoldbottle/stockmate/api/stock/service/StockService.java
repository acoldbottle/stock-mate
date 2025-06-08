package com.acoldbottle.stockmate.api.stock.service;

import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.external.kis.stockfile.StockDTO;
import com.acoldbottle.stockmate.external.kis.stockfile.StockFileDownloader;
import com.acoldbottle.stockmate.external.kis.stockfile.StockFileParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final StockFileDownloader fileDownloader;
    private final StockFileParser fileParser;

    @Transactional
    public void updateStockDB() {

        fileDownloader.downloadAll();
        List<StockDTO> stockDTOS = fileParser.parseAll();
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
