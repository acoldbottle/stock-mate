package com.acoldbottle.stockmate.currentprice.service;

import com.acoldbottle.stockmate.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.external.kis.KisAPIClient;
import com.acoldbottle.stockmate.external.kis.KisCurrentPriceRes;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrentPriceService {

    private final Bucket bucket;
    private final KisAPIClient kisAPIClient;
    private final CurrentPriceCacheService cacheService;

    public KisCurrentPriceRes requestCurrentPriceToKisAPI(String symbol, String marketCode, ExecutorService executor) throws InterruptedException {
        if (bucket.asBlocking().tryConsume(1, Duration.ofSeconds(50))) {
            CompletableFuture<KisCurrentPriceRes> kisCurrentPriceFuture = CompletableFuture.supplyAsync(() ->
                    kisAPIClient.requestCurrentPrice(symbol, marketCode), executor)
                    .exceptionally(ex -> {
                        log.error("=== KIS API 현재가 가져오는 과정에서 오류 발생 ===]", ex);
                        return null;
                    });
            return kisCurrentPriceFuture.join();
        } else {
            log.warn("=== KIS API 현재가 요청 실패 ===");
            log.warn("=== 스레드 대기 시간 초과 ===");
            return null;
        }
    }

    public CompletableFuture<Void> updateCurrentPrice(String symbol, KisCurrentPriceRes kisCurrentPriceRes, ExecutorService executor) {
        if (kisCurrentPriceRes == null) {
            log.warn("kisCurrentPriceRes == null, 현재가 업데이트 생략 -> symbol={}", symbol);
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() ->
                        CurrentPriceDTO.from(kisCurrentPriceRes), executor)
                .exceptionally(ex -> {
                    log.error("=== 레디스에 저장 실패 ===");
                    log.error("=== DTO:{}, symbol:{} ===", kisCurrentPriceRes, symbol);
                    return null;
                })
                .thenAccept(dto -> cacheService.updateCurrentPrice(symbol, dto));
    }
}
