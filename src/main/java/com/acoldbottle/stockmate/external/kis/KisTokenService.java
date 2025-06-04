package com.acoldbottle.stockmate.external.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.kis.KisTokenReissueException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KisTokenService {

    private final KisTokenRepository kisRepository;
    private final RestClient restClient;

    @Value("${kis.appkey}")
    private String key;
    @Value("${kis.appsecret}")
    private String secret;

    public void reissueToken() {
        KisTokenReissueReq request = KisTokenReissueReq.builder()
                .grant_type("client_credentials")
                .appkey(key)
                .appsecret(secret)
                .build();

        KisTokenReissueRes response = restClient.post()
                .uri("/oauth2/tokenP")
                .body(request)
                .retrieve()
                .body(KisTokenReissueRes.class);

        if (response == null || response.getToken() == null) {
            throw new KisTokenReissueException(ErrorCode.KIS_TOKEN_REISSUE_FAILED);
        }
        kisRepository.deleteById(1L);

    }
}
