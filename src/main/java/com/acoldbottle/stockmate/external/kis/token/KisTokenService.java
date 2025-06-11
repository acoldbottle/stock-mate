package com.acoldbottle.stockmate.external.kis.token;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.kis.KisTokenReissueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class KisTokenService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String REDIS_TOKEN_KEY = "kis:access-token";
    private final RedisTemplate<String, String> redisTemplate;
    private final RestClient restClient;
    @Value("${kis.appkey}")
    private String key;
    @Value("${kis.appsecret}")
    private String secret;

    public synchronized String getValidToken() {
        String token = redisTemplate.opsForValue().get(REDIS_TOKEN_KEY);
        if (token == null) {
            return "Bearer " + reissueToken();
        }
        return "Bearer " + token;
    }

    private String reissueToken() {
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
        String token = response.getToken();
        LocalDateTime tokenExpired = LocalDateTime.parse(response.getTokenExpired(), FORMATTER);
        long ttl = Duration.between(LocalDateTime.now(), tokenExpired).getSeconds() - 600;
        redisTemplate.opsForValue().set(REDIS_TOKEN_KEY, token, Duration.ofSeconds(ttl));

        log.info("===== token reissue =====");
        log.info("token = {}", response.getToken());
        log.info("token expired = {}", response.getTokenExpired());
        log.info("ttl -> seconds : {}", ttl);
        log.info("===== token reissue =====");

        return token;
    }
}
