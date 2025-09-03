package com.acoldbottle.stockmate.scheduler;

import com.acoldbottle.stockmate.api.stock.service.StockService;
import com.acoldbottle.stockmate.event.email.EmailAlertEvent;
import com.acoldbottle.stockmate.external.kis.stockfile.StockDTO;
import com.acoldbottle.stockmate.external.kis.stockfile.StockFileDownloader;
import com.acoldbottle.stockmate.external.kis.stockfile.StockFileParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockUpdateScheduler {

    private final StockService stockService;
    private final StockFileDownloader fileDownloader;
    private final StockFileParser fileParser;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 4 * * SUN", zone = "Asia/Seoul")
    public void updateStockDB() {
        try {
            fileDownloader.downloadAll();
            List<StockDTO> stockDTOS = fileParser.parseAll();
            stockService.updateStocks(stockDTOS);
        } catch (Exception e) {
            log.error("=== [StockUpdateScheduler] 주식 종목 업데이트 실패 ===", e);
            eventPublisher.publishEvent(new EmailAlertEvent("[StockUpdateScheduler] 예외 발생 --> " + e.getMessage()));
        }
    }
}
