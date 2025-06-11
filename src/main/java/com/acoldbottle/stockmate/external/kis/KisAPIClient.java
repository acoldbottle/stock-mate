package com.acoldbottle.stockmate.external.kis;

import com.acoldbottle.stockmate.external.kis.token.KisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class KisAPIClient {

    @Value("${kis.appkey}")
    private final String key;
    @Value("${kis.appsecret}")
    private final String secret;
    @Value("${kis.tr-id}")
    private final String trId;
    @Value("${kis.custtype}")
    private final String custtype;

    private final RestClient restClient;
    private final KisTokenService kisTokenService;

    public KisCurrentPriceRes requestCurrentPrice(String Symbol, String marketCode) {
        KisCurrentPriceReq req = KisCurrentPriceReq.builder()
                .appkey(key)
                .appsecret(secret)
                .Authorization(kisTokenService.getValidToken())
                .tr_id(trId)
                .custtype(custtype)
                .build();

        return null;
    }
}
