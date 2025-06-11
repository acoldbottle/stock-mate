package com.acoldbottle.stockmate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     *
     * 레디스를 활용한 캐시 서비스(현재가,등락률 조회), KisClient에서는 외부 api 에 요청만 하고 dto 로 반환 -> 레디스에 저장
     *
     * Async 서비스도 만들어서 여기에서 레디스 서비스를 주입해 레디스에 저장하는 작업을 @Async를 붙여 수행
     *
     * holding 서비스에서는 캐시서비스를 주입받아 symbol로 레디스에서 캐싱해서 수익률 계산.
     */

}
