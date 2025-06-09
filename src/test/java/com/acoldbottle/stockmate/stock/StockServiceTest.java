package com.acoldbottle.stockmate.stock;

import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import com.acoldbottle.stockmate.api.stock.service.StockService;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class StockServiceTest {

    @Autowired
    StockService stockService;

    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    @Transactional
    void saveStock() {
        Stock stock1 = Stock.builder()
                .symbol("APPL")
                .korName("애플")
                .engName("APPLE INC")
                .marketCode("NASDAQ")
                .build();

        Stock stock2 = Stock.builder()
                .symbol("TSLA")
                .korName("테슬라")
                .engName("TESLA")
                .marketCode("NASDAQ")
                .build();

        stockRepository.save(stock1);
        stockRepository.save(stock2);
    }

    @Test
    @DisplayName("주식 검색 성공")
    void search_success() {
        String keyword = "apple";

        List<StockSearchRes> result = stockService.searchByKeyword(keyword);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

//    @Test
    @DisplayName("주식 검색 실패 -> 빈칸으로 검색")
    void search_failed() {
        String keyword = "  ";
    }
}
