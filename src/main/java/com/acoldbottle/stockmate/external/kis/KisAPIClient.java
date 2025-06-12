package com.acoldbottle.stockmate.external.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.kis.KisCurrentPriceException;
import com.acoldbottle.stockmate.external.kis.token.KisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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

    public KisCurrentPriceRes requestCurrentPrice(String symbol, String marketCode) {
        String token = kisTokenService.getValidToken();

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
    }
}
