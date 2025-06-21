package com.acoldbottle.stockmate.currentprice.service;

import com.acoldbottle.stockmate.currentprice.dto.CurrentPriceDTO;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrentPriceCacheService {

    @CachePut(value = "currentPriceCache", key = "'stocks:' + #symbol")
    public CurrentPriceDTO updateCurrentPrice(String symbol, CurrentPriceDTO currentPriceDTO) {
        return currentPriceDTO;
    }

    @Cacheable(value = "currentPriceCache", key = "'stocks:' + #symbol")
    public CurrentPriceDTO getCurrentPrice(String symbol) {
        return null;
    }
}
