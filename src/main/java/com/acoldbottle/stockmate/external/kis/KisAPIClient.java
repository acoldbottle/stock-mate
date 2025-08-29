package com.acoldbottle.stockmate.external.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.kis.KisCurrentPriceException;
import com.acoldbottle.stockmate.exception.kis.KisTooManyRequestException;
import com.acoldbottle.stockmate.external.kis.token.KisTokenService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisAPIClient {

    @Value("${kis.appkey}")
    private String key;
    @Value("${kis.appsecret}")
    private String secret;
    @Value("${kis.tr-id}")
    private String trId;
    @Value("${kis.custtype}")
    private String custtype;

    private final RestClient restClient;
    private final KisTokenService kisTokenService;

    private final Bucket bucket;

    public KisCurrentPriceRes requestCurrentPrice(String symbol, String marketCode) throws InterruptedException {

        String token = kisTokenService.getValidToken();

        if (bucket.asBlocking().tryConsume(1, Duration.ofSeconds(59))) {
            KisCurrentPriceRes res = restClient.get()
                    .uri("/uapi/overseas-price/v1/quotations/price?AUTH=y&EXCD=" + marketCode + "&SYMB=" + symbol)
                    .header("Authorization", token)
                    .header("appkey", key)
                    .header("appsecret", secret)
                    .header("tr_id", trId)
                    .header("custtype", custtype)
                    .retrieve()
                    .body(KisCurrentPriceRes.class);
            if (res == null || !res.getRt_cd().equals("0")) {
                throw new KisCurrentPriceException(ErrorCode.KIS_CURRENT_PRICE_ERROR);
            }
            return res;
        } else {
            throw new KisTooManyRequestException(ErrorCode.KIS_TOO_MANY_REQUEST);
        }
    }
}
