package com.acoldbottle.stockmate.api.portfolio.manager;

import com.acoldbottle.stockmate.api.holding.manager.HoldingManager;
import com.acoldbottle.stockmate.event.portfolio.PortfolioSubscriberEvent;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.acoldbottle.stockmate.exception.ErrorCode.PORTFOLIO_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class PortfolioManager {

    private final PortfolioRepository portfolioRepository;
    private final HoldingManager holdingManager;
    private final ApplicationEventPublisher eventPublisher;

    public Portfolio get(Long portfolioId, User user) {
        return portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND));
    }

    public List<Portfolio> getPortfolioList(Long userId) {
        return portfolioRepository.findAllByUserId(userId);
    }

    public List<Portfolio> getPortfolioListByPortfolioIds(List<Long> portfolioIds) {
        return portfolioRepository.findAllWithUserByIds(portfolioIds);
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
        List<Holding> holdingList = holdingManager.getHoldingList(portfolio.getId());

        holdingManager.deleteHoldingList(portfolio);
        portfolioRepository.delete(portfolio);
        eventPublisher.publishEvent(new PortfolioSubscriberEvent(portfolio, holdingList));
    }
}
