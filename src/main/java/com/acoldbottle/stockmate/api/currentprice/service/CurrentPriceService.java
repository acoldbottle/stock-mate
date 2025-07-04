package com.acoldbottle.stockmate.api.currentprice.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.exception.kis.KisRequestInterruptedException;
import com.acoldbottle.stockmate.exception.kis.KisTooManyRequestException;
import com.acoldbottle.stockmate.external.kis.KisAPIClient;
import com.acoldbottle.stockmate.external.kis.KisCurrentPriceRes;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

import static com.acoldbottle.stockmate.exception.ErrorCode.KIS_REQUEST_INTERRUPTED_ERROR;
import static com.acoldbottle.stockmate.exception.ErrorCode.KIS_TOO_MANY_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceService {

    private final Bucket bucket;
    private final KisAPIClient kisAPIClient;
    private final CurrentPriceCacheService cacheService;

    public CompletableFuture<KisCurrentPriceRes> requestCurrentPriceToKisAPI(String symbol, String marketCode, ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (bucket.asBlocking().tryConsume(1, Duration.ofSeconds(59))) {
                    return kisAPIClient.requestCurrentPrice(symbol, marketCode);
                } else {
                    throw new CompletionException(new KisTooManyRequestException(KIS_TOO_MANY_REQUEST));
                }
            } catch (InterruptedException e) {
                throw new CompletionException(new KisRequestInterruptedException(KIS_REQUEST_INTERRUPTED_ERROR));
            }
        }, executor);
    }

    public CompletableFuture<Void> updateCurrentPrice(String symbol, KisCurrentPriceRes kisCurrentPriceRes, ExecutorService executor) {
        if (kisCurrentPriceRes == null) {
            log.warn("kisCurrentPriceRes == null, 현재가 업데이트 생략 -> symbol={}", symbol);
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() ->
                        CurrentPriceDTO.from(kisCurrentPriceRes), executor)
                .thenAccept(dto -> cacheService.updateCurrentPrice(symbol, dto))
                .exceptionally(ex -> {
                    log.error("=== [CurrentPriceService] 현재가 업데이트 중 오류 발생 DTO:{}, symbol:{} ===", kisCurrentPriceRes, symbol);
                    return null;
                });
    }
}
