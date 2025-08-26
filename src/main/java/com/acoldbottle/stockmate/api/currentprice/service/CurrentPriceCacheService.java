package com.acoldbottle.stockmate.api.currentprice.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentPriceCacheService {

    private final RedisTemplate<String, CurrentPriceDTO> redisTemplate;

    public CurrentPriceDTO getCurrentPrice(String symbol) {
        String key = "stocks:" + symbol;
        return redisTemplate.opsForValue().get(key);
    }

    public boolean updateCurrentPrice(String symbol, CurrentPriceDTO newPrice) {
        String key = "stocks:" + symbol;
        CurrentPriceDTO oldPrice = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set(key, newPrice, Duration.ofDays(2));
        return oldPrice == null || !oldPrice.equals(newPrice);
    }

}
