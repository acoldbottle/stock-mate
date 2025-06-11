package com.acoldbottle.stockmate.config;

import com.acoldbottle.stockmate.currentprice.dto.CurrentPriceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

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
     * 레디스를 활용한 캐시 서비스(현재가,등락률 조회), KisClient에서는 외부 api 에 요청만 하고 dto 로 반환 -> 레디스에 저장
     * <p>
     * Async 서비스도 만들어서 여기에서 레디스 서비스를 주입해 레디스에 저장하는 작업을 @Async를 붙여 수행
     * <p>
     * holding 서비스에서는 캐시서비스를 주입받아 symbol로 레디스에서 캐싱해서 수익률 계산.
     */
    @Bean
    public RedisTemplate<String, CurrentPriceDTO> redisTemplateForCurrentPrice(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CurrentPriceDTO> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<CurrentPriceDTO> serializer = new Jackson2JsonRedisSerializer<>(CurrentPriceDTO.class);
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10L));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
