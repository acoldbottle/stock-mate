package com.acoldbottle.stockmate.api.portfolio.service;

import com.acoldbottle.stockmate.api.holding.service.HoldingManager;
import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioSubscriberRegistry;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.acoldbottle.stockmate.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class PortfolioManager {

    private final PortfolioRepository portfolioRepository;
    private final HoldingManager holdingManager;
    private final PortfolioSubscriberRegistry subscriberRegistry;

    public Portfolio get(Long portfolioId, User user) {
        return portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND));
    }

    public List<Portfolio> getPortfolioList(Long userId) {
        return portfolioRepository.findAllByUserId(userId);
    }

    public Portfolio create(User user, String title) {
        return portfolioRepository.save(
                Portfolio.builder()
                        .user(user)
                        .title(title)
                        .build()
        );
    }

    public void delete(Portfolio portfolio) {
        holdingManager.deleteHoldingList(portfolio);

        portfolioRepository.delete(portfolio);
        subscriberRegistry.unregister(portfolio.getId());
    }
}
