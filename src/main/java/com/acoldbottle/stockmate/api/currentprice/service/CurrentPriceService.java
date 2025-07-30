package com.acoldbottle.stockmate.api.currentprice.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.exception.kis.KisRequestInterruptedException;
import com.acoldbottle.stockmate.exception.kis.KisTooManyRequestException;
import com.acoldbottle.stockmate.exception.kis.KisUpdateException;
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

import static com.acoldbottle.stockmate.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceService {

    private final Bucket bucket;
    private final KisAPIClient kisAPIClient;
    private final CurrentPriceCacheService cacheService;


    public CompletableFuture<Void> requestAndUpdateCurrentPrice(String symbol, String marketCode, ExecutorService executor) {
        return requestCurrentPriceToKisAPI(symbol, marketCode, executor)
                .thenCompose(kisCurrentPriceRes -> updateCurrentPrice(symbol, kisCurrentPriceRes, executor));
    }

    private CompletableFuture<KisCurrentPriceRes> requestCurrentPriceToKisAPI(String symbol, String marketCode, ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (bucket.asBlocking().tryConsume(1, Duration.ofSeconds(59))) {
                    return kisAPIClient.requestCurrentPrice(symbol, marketCode);
                } else {
                    log.error("=== [CurrentPriceService] 현재가 업데이트 중 현재 시세 요청 실패 ===");
                    throw new CompletionException(new KisTooManyRequestException(KIS_TOO_MANY_REQUEST));
                }
            } catch (InterruptedException e) {
                log.error("=== [CurrentPriceService] 현재가 업데이트 중 인터럽트 발생 ! ===");
                throw new CompletionException(new KisRequestInterruptedException(KIS_REQUEST_INTERRUPTED_ERROR));
            }
        }, executor);
    }

    private CompletableFuture<Void> updateCurrentPrice(String symbol, KisCurrentPriceRes kisCurrentPriceRes, ExecutorService executor) {
        if (kisCurrentPriceRes == null) {
            log.warn("kisCurrentPriceRes == null, 현재가 업데이트 생략 -> symbol={}", symbol);
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() ->
                        CurrentPriceDTO.from(kisCurrentPriceRes), executor)
                .thenAccept(dto -> cacheService.updateCurrentPrice(symbol, dto))
                .exceptionally(ex -> {
                    log.error("=== [CurrentPriceService] 현재가 업데이트 중 레디스에 저장 실패 ===");
                    throw new CompletionException(new KisUpdateException(KIS_UPDATE_ERROR));
                });
    }
}
