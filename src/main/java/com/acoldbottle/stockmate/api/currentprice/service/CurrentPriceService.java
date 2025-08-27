package com.acoldbottle.stockmate.api.currentprice.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.event.holding.HoldingSseNotifyEvent;
import com.acoldbottle.stockmate.event.portfolio.PortfolioSseNotifyEvent;
import com.acoldbottle.stockmate.event.watchlist.WatchlistSseNotifyEvent;
import com.acoldbottle.stockmate.exception.kis.KisTooManyRequestException;
import com.acoldbottle.stockmate.external.kis.KisAPIClient;
import com.acoldbottle.stockmate.external.kis.KisCurrentPriceRes;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.acoldbottle.stockmate.exception.ErrorCode.KIS_TOO_MANY_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceService {

    private final Bucket bucket;
    private final KisAPIClient kisAPIClient;
    private final CurrentPriceCacheService cacheService;
    private final ApplicationEventPublisher eventPublisher;


    public void requestAndUpdateCurrentPrice(String symbol, String marketCode) {
        try {
            KisCurrentPriceRes kisCurrentPriceRes = requestCurrentPrice(symbol, marketCode);
            updateCurrentPrice(kisCurrentPriceRes, symbol);
        } catch (InterruptedException e) {
            log.warn("=== [CurrentPriceService] 인터럽트 발생 ! ===");
            Thread.currentThread().interrupt();
        }
    }

    private KisCurrentPriceRes requestCurrentPrice(String symbol, String marketCode) throws InterruptedException{
        if (bucket.asBlocking().tryConsume(1, Duration.ofSeconds(59))) {
            return kisAPIClient.requestCurrentPrice(symbol, marketCode);
        } else {
            log.error("=== [CurrentPriceService] 현재가 업데이트 중 현재 시세 요청 실패 ===");
            throw new KisTooManyRequestException(KIS_TOO_MANY_REQUEST);
        }
    }

    private void updateCurrentPrice(KisCurrentPriceRes kisCurrentPriceRes, String symbol) {
        if (kisCurrentPriceRes==null) {
            log.warn("kisCurrentPriceRes == null, 현재가 업데이트 생략 -> symbol={}", symbol);
            return;
        }
        CurrentPriceDTO currentPriceDto = CurrentPriceDTO.from(kisCurrentPriceRes);
        boolean isUpdated = cacheService.updateCurrentPrice(symbol, currentPriceDto);
        if (isUpdated) {
            eventPublisher.publishEvent(new PortfolioSseNotifyEvent(symbol, currentPriceDto));
            eventPublisher.publishEvent(new HoldingSseNotifyEvent(symbol, currentPriceDto));
            eventPublisher.publishEvent(new WatchlistSseNotifyEvent(symbol, currentPriceDto));
        }
    }


}
