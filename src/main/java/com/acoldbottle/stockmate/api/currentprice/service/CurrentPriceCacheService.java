package com.acoldbottle.stockmate.api.currentprice.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CurrentPriceCacheService {

    @Cacheable(value = "currentPriceCache", key = "'stocks:' + #symbol")
    public CurrentPriceDTO getCurrentPrice(String symbol) {
        return null;
    }

    @CachePut(value = "currentPriceCache", key = "'stocks:' + #symbol")
    public CurrentPriceDTO updateCurrentPrice(String symbol, CurrentPriceDTO currentPriceDTO) {

        return currentPriceDTO;
    }
}
