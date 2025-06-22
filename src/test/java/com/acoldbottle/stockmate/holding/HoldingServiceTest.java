package com.acoldbottle.stockmate.holding;

import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingCreateRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingUpdateRes;
import com.acoldbottle.stockmate.api.holding.service.HoldingService;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.stock.StockNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class HoldingServiceTest {

    @Autowired
    HoldingService holdingService;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    UserRepository userRepository;

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
    @DisplayName("포트폴리오에_주식_추가_성공")
    void create_holding_success() {
        HoldingCreateReq createReq = new HoldingCreateReq();
        createReq.setSymbol("AAPL");
        createReq.setQuantity(10);
        createReq.setPurchasePrice(BigDecimal.valueOf(100.88));
        User user = userRepository.findByUsername("root").get();
        Portfolio portfolio = portfolioRepository.findAllByUser(user).get(0);
        HoldingCreateRes savedHolding = holdingService.createHolding(user.getId(), portfolio.getId(), createReq);
        assertThat(savedHolding.getId()).isEqualTo(1L);
    }

    @Test
    @Transactional
    @DisplayName("포트폴리오에_주식_추가_실패_없는_종목")
    void create_holding_failed_not_exists() {
        HoldingCreateReq createReq = new HoldingCreateReq();
        createReq.setSymbol("XXX");
        createReq.setQuantity(10);
        createReq.setPurchasePrice(BigDecimal.valueOf(100.88));
        User user = userRepository.findByUsername("root").get();
        Portfolio portfolio = portfolioRepository.findAllByUser(user).get(0);
        assertThatThrownBy(() ->
                holdingService.createHolding(user.getId(), portfolio.getId(), createReq))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    @Transactional
    @DisplayName("포트폴리오에_주식_추가_실패_수량_0")
    void create_holding_failed_quantity_zero() {
        HoldingCreateReq createReq = new HoldingCreateReq();
        createReq.setSymbol("AAPL");
        createReq.setQuantity(0);
        createReq.setPurchasePrice(BigDecimal.valueOf(100.88));
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<HoldingCreateReq>> wrongQuantity = validator.validate(createReq);

        assertThat(wrongQuantity).isNotEmpty();
    }

    @Test
    @Transactional
    @DisplayName("포트폴리오에_주식_추가_실패_매수가_0")
    void create_holding_failed_price_zero() {
        HoldingCreateReq createReq = new HoldingCreateReq();
        createReq.setSymbol("AAPL");
        createReq.setQuantity(10);
        createReq.setPurchasePrice(BigDecimal.ZERO);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<HoldingCreateReq>> wrongPrice = validator.validate(createReq);

        assertThat(wrongPrice).isNotEmpty();
    }

    @Test
    @DisplayName("포트폴리오_주식_수정_성공")
    void update_holding_success() {
        User user = userRepository.findByUsername("root").get();
        Portfolio portfolio = portfolioRepository.findAllByUser(user).get(0);
        saveHolding();

        HoldingUpdateReq holdingUpdateReq = new HoldingUpdateReq();
        holdingUpdateReq.setQuantity(1000);
        holdingUpdateReq.setPurchasePrice(BigDecimal.valueOf(120.88));

        HoldingUpdateRes updatedHolding = holdingService.updateHolding(user.getId(), portfolio.getId(), 1L, holdingUpdateReq);
        assertThat(updatedHolding.getQuantity()).isEqualTo(1000);
    }

    @Test
    @DisplayName("포트폴리오에_주식_수정_실패_수량_0")
    void update_holding_failed_quantity_zero() {
        User user = userRepository.findByUsername("root").get();
        Portfolio portfolio = portfolioRepository.findAllByUser(user).get(0);
        saveHolding();

        HoldingUpdateReq holdingUpdateReq = new HoldingUpdateReq();
        holdingUpdateReq.setQuantity(0);
        holdingUpdateReq.setPurchasePrice(BigDecimal.valueOf(120.88));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<HoldingUpdateReq>> wrongQuantity = validator.validate(holdingUpdateReq);

        assertThat(wrongQuantity).isNotEmpty();
    }

    @Test
    @DisplayName("포트폴리오에_주식_수정_실패_매수가_0")
    void update_holding_failed_price_zero() {
        User user = userRepository.findByUsername("root").get();
        Portfolio portfolio = portfolioRepository.findAllByUser(user).get(0);
        saveHolding();

        HoldingUpdateReq holdingUpdateReq = new HoldingUpdateReq();
        holdingUpdateReq.setQuantity(1000);
        holdingUpdateReq.setPurchasePrice(BigDecimal.ZERO);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<HoldingUpdateReq>> wrongPrice = validator.validate(holdingUpdateReq);

        assertThat(wrongPrice).isNotEmpty();
    }

    void saveHolding() {
        HoldingCreateReq createReq = new HoldingCreateReq();
        createReq.setSymbol("AAPL");
        createReq.setQuantity(10);
        createReq.setPurchasePrice(BigDecimal.valueOf(100.88));
        User user = userRepository.findByUsername("root").get();
        Portfolio portfolio = portfolioRepository.findAllByUser(user).get(0);
        HoldingCreateRes savedHolding = holdingService.createHolding(user.getId(), portfolio.getId(), createReq);
    }
}
