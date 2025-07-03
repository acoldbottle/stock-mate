package com.acoldbottle.stockmate.currentprice.service;

import com.acoldbottle.stockmate.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.external.kis.KisCurrentPriceRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;

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
