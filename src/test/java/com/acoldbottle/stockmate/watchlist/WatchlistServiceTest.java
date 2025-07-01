package com.acoldbottle.stockmate.watchlist;

import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemCreateRes;
import com.acoldbottle.stockmate.api.watchlist.service.WatchlistService;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.stock.StockNotFoundException;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class WatchlistServiceTest {

    @Autowired
    StockRepository stockRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    WatchlistService watchlistService;

    @BeforeEach
    @Transactional
    void initData() {
        User savedUser = userRepository.save(User.builder()
                .username("root")
                .password("root1234")
                .build());
        stockRepository.save(Stock.builder()
                .symbol("AAPL")
                .marketCode("NAS")
                .korName("애플")
                .engName("apple")
                .build());
        stockRepository.save(Stock.builder()
                .symbol("TSLA")
                .marketCode("NAS")
                .korName("테슬라")
                .engName("tesla")
                .build());
        portfolioRepository.save(Portfolio.builder()
                .title("portfolio1")
                .user(savedUser)
                .build());
    }

    @Test
    @Transactional
    @DisplayName("관심_종목_등록_성공")
    void create_watchlist_success() {
        User user = userRepository.findByUsername("root").get();
        WatchItemCreateReq watchItemCreateReq = new WatchItemCreateReq();
        watchItemCreateReq.setSymbol("AAPL");
        WatchItemCreateRes watchItemCreateRes = watchlistService.createWatchItem(user.getId(), watchItemCreateReq);
        assertThat(watchItemCreateRes.getSymbol()).isEqualTo("AAPL");
    }

    @Test
    @Transactional
    @DisplayName("관심_종목_등록_실패_없는_주식")
    void create_watchlist_failed_not_exists_stock() {
        User user = userRepository.findByUsername("root").get();
        WatchItemCreateReq watchItemCreateReq = new WatchItemCreateReq();
        watchItemCreateReq.setSymbol("AAPLA");
        assertThatThrownBy(() -> watchlistService.createWatchItem(user.getId(), watchItemCreateReq))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    @Transactional
    @DisplayName("관심_종목_등록_실패_이미_등록한_주식")
    void create_watchlist_failed_already_exists_stock() {
        User user = userRepository.findByUsername("root").get();
        WatchItemCreateReq watchItemCreateReq = new WatchItemCreateReq();
        watchItemCreateReq.setSymbol("AAPL");
        watchlistService.createWatchItem(user.getId(), watchItemCreateReq);

        assertThatThrownBy(() -> watchlistService.createWatchItem(user.getId(), watchItemCreateReq))
                .isInstanceOf(WatchItemAlreadyExistsException.class);
    }

    @Test
    @Transactional
    @DisplayName("관심_종목_조회_성공")
    void get_watchlist_success() {
        User user = userRepository.findByUsername("root").get();

        WatchItemCreateReq watchItem1 = new WatchItemCreateReq();
        WatchItemCreateReq watchItem2 = new WatchItemCreateReq();
        watchItem1.setSymbol("AAPL");
        watchItem2.setSymbol("TSLA");
        watchlistService.createWatchItem(user.getId(), watchItem1);
        watchlistService.createWatchItem(user.getId(), watchItem2);

        assertThat(watchlistService.getWatchlist(user.getId()).size())
                .isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("관심_종목_삭제_성공")
    void delete_watch_item_success() {
        User user = userRepository.findByUsername("root").get();

        WatchItemCreateReq watchItem1 = new WatchItemCreateReq();
        WatchItemCreateReq watchItem2 = new WatchItemCreateReq();
        watchItem1.setSymbol("AAPL");
        watchItem2.setSymbol("TSLA");
        WatchItemCreateRes res1 = watchlistService.createWatchItem(user.getId(), watchItem1);
        WatchItemCreateRes res2 = watchlistService.createWatchItem(user.getId(), watchItem2);

        watchlistService.deleteWatchItem(user.getId(), res1.getWatchItemId());
        assertThat(watchlistService.getWatchlist(user.getId()).size())
                .isEqualTo(1);
    }
}
