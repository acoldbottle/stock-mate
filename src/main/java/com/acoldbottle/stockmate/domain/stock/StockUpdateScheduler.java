package com.acoldbottle.stockmate.domain.stock;

import com.acoldbottle.stockmate.api.stock.service.StockService;
import com.acoldbottle.stockmate.external.kis.stockfile.StockDTO;
import com.acoldbottle.stockmate.external.kis.stockfile.StockFileDownloader;
import com.acoldbottle.stockmate.external.kis.stockfile.StockFileParser;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockUpdateScheduler {

    private final StockService stockService;
    private final StockFileDownloader fileDownloader;
    private final StockFileParser fileParser;

    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void updateStockDB() {
        fileDownloader.downloadAll();
        List<StockDTO> stockDTOS = fileParser.parseAll();
        stockService.saveStocks(stockDTOS);
    }
}
