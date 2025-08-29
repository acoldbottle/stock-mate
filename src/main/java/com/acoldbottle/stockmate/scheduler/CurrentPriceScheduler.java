package com.acoldbottle.stockmate.scheduler;

import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceService;
import com.acoldbottle.stockmate.event.email.EmailAlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceScheduler {

    private final CurrentPriceService currentPriceService;
    private final ApplicationEventPublisher eventPublisher;
    private final static int MONDAY = 1;
    private final static int TUESDAY = 2;
    private final static int FRIDAY = 5;
    private final static int SATURDAY = 6;

    private final static int MARKET_OPEN_HOUR = 16;
    private final static int MARKET_CLOSE_HOUR = 7;


    @Scheduled(fixedRate = 60000L)
    public void requestCurrentPriceAndCache() {
        if (!isMarketTime()) return;

        try {
            currentPriceService.requestAndUpdateStocks();
        } catch (Exception e) {
            log.error("=== [CurrentPriceScheduler] 예외 발생 ! ===", e);
            eventPublisher.publishEvent(new EmailAlertEvent("[CurrentPriceScheduler] 예외 발생 --> " + e.getMessage()));
        }
    }

    private boolean isMarketTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        int nowDay = now.getDayOfWeek().getValue();
        int nowHour = now.getHour();

        if (nowDay >= MONDAY && nowDay <= FRIDAY && nowHour >= MARKET_OPEN_HOUR) return true;
        if (nowDay >= TUESDAY && nowDay <= SATURDAY && nowHour < MARKET_CLOSE_HOUR) return true;

        return false;
    }
}
