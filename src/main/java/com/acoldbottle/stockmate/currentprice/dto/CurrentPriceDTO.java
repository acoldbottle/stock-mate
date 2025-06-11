package com.acoldbottle.stockmate.currentprice.dto;

import com.acoldbottle.stockmate.external.kis.KisCurrentPriceRes;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Getter
@Builder
public class CurrentPriceDTO {

    private BigDecimal last;
    private BigDecimal rate;

    public static CurrentPriceDTO from(KisCurrentPriceRes res) {
        return CurrentPriceDTO.builder()
                .last(parseBigDecimal(res.getOutput().getLast()))
                .rate(parseBigDecimal(res.getOutput().getRate()))
                .build();
    }

    private static BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.warn("=== 한국투자증권 OPEN API 요청 응답을 파싱 실패 ===");
            log.warn("=== 기본값 0 반환, 응답받은 값 : {} ===", value);
            return BigDecimal.ZERO;
        }
    }
}
