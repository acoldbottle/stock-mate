package com.acoldbottle.stockmate.portfolio;

import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioUpdateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioCreateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioUpdateRes;
import com.acoldbottle.stockmate.api.portfolio.service.PortfolioService;
import com.acoldbottle.stockmate.api.user.dto.req.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.res.UserSignUpRes;
import com.acoldbottle.stockmate.api.user.service.UserService;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class PortfolioServiceTest {

    @Autowired
    PortfolioService portfolioService;

    @Autowired
    UserService userService;

    @Autowired
    Validator validator;


    @Test
    @Transactional
    @DisplayName("포트폴리오_생성_성공")
    void create_portfolio_success() {
        init_user();
        PortfolioCreateReq portfolioCreateReq = new PortfolioCreateReq();
        portfolioCreateReq.setTitle("포트폴리오1");
        PortfolioCreateRes portfolio = portfolioService.createPortfolio(1L, portfolioCreateReq);

        assertThat(portfolio.getTitle()).isEqualTo(portfolioCreateReq.getTitle());
    }

    @Test
    @Transactional
    @DisplayName("포트폴리오_생성_실패 -> PortfolioCreateReq 검증 오류")
    void create_portfolio_failed() {
        init_user();
        PortfolioCreateReq portfolioCreateReq = new PortfolioCreateReq();
        portfolioCreateReq.setTitle("   ");

        assertThat(validator.validate(portfolioCreateReq).size())
                .isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("포트폴리오_조회")
    void get_portfolioList() {
        init_user();
        for (int i = 0; i < 10; i++) {
            PortfolioCreateReq portfolioCreateReq = new PortfolioCreateReq();
            portfolioCreateReq.setTitle("portfolio" + i);
            portfolioService.createPortfolio(1L, portfolioCreateReq);
        }

        assertThat(portfolioService.getPortfolioList(1L).size())
                .isEqualTo(10);
    }

    @Test
    @Transactional
    @DisplayName("포트폴리오_수정")
    void update_portfolio() {
        init_user();
        for (int i = 0; i < 10; i++) {
            PortfolioCreateReq portfolioCreateReq = new PortfolioCreateReq();
            portfolioCreateReq.setTitle("portfolio" + i);
            portfolioService.createPortfolio(1L, portfolioCreateReq);
        }

        PortfolioUpdateReq portfolioUpdateReq = new PortfolioUpdateReq();
        portfolioUpdateReq.setTitle("updated Test");
        PortfolioUpdateRes updatedPortfolio = portfolioService.updatePortfolio(1L, 9L, portfolioUpdateReq);

        assertThat(updatedPortfolio.getTitle())
                .isEqualTo("updated Test");
    }

    @Test
    @Transactional
    @DisplayName("포트폴리오_수정_실패 -> 수정하려는 포트폴리오 존재 x")
    void update_portfolio_failed() {
        init_user();
        for (int i = 0; i < 10; i++) {
            PortfolioCreateReq portfolioCreateReq = new PortfolioCreateReq();
            portfolioCreateReq.setTitle("portfolio" + i);
            portfolioService.createPortfolio(1L, portfolioCreateReq);
        }

        PortfolioUpdateReq portfolioUpdateReq = new PortfolioUpdateReq();
        portfolioUpdateReq.setTitle("updated Test");

        assertThatThrownBy(() ->
                portfolioService.updatePortfolio(1L, 100L, portfolioUpdateReq))
                .isInstanceOf(PortfolioNotFoundException.class);
    }

    void init_user() {
        UserSignUpRes savedUser = userService.signUp(UserSignUpReq.builder()
                .username("user_test1")
                .password("a12345678")
                .passwordConfirm("a12345678")
                .build());
    }
}
