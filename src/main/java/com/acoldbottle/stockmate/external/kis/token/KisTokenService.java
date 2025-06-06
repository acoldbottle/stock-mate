package com.acoldbottle.stockmate.external.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.kis.KisTokenReissueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class KisTokenService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Long TOKEN_ID = 1L;

    private final KisTokenRepository kisRepository;
    private final RestClient restClient;
    @Value("${kis.appkey}")
    private String key;
    @Value("${kis.appsecret}")
    private String secret;

    @Transactional
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

        log.info("===== token reissue =====");
        log.info("token = {}", response.getToken());
        log.info("token expired = {}", response.getTokenExpired());
        log.info("===== token reissue =====");

        if (response == null || response.getToken() == null) {
            throw new KisTokenReissueException(ErrorCode.KIS_TOKEN_REISSUE_FAILED);
        }
        String newKisToken = response.getToken();
        LocalDateTime tokenExpired = LocalDateTime.parse(response.getTokenExpired(), FORMATTER);
        KisToken oldKisToken = kisRepository.findById(TOKEN_ID).orElse(null);
        if (oldKisToken == null) {
            kisRepository.save(KisToken.builder()
                    .id(TOKEN_ID)
                    .token(newKisToken)
                    .tokenExpired(tokenExpired)
                    .build());
        } else {
            oldKisToken.updateReissueToken(newKisToken, tokenExpired);
        }
    }
}
