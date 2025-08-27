package com.acoldbottle.stockmate.api.holding.manager;

import com.acoldbottle.stockmate.event.holding.HoldingSubscriberEvent;
import com.acoldbottle.stockmate.api.trackedsymbol.manager.TrackedSymbolManager;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.exception.holding.HoldingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.acoldbottle.stockmate.event.holding.HoldingSubscriberEvent.HoldingEventType.*;
import static com.acoldbottle.stockmate.exception.ErrorCode.HOLDING_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class HoldingManager {

    private final HoldingRepository holdingRepository;
    private final TrackedSymbolManager trackedSymbolManager;
    private final ApplicationEventPublisher eventPublisher;

    public List<Holding> getHoldingList(Long portfolioId) {
        return holdingRepository.findAllWithStockByPortfolioId(portfolioId);
    }

    public List<Holding> getHoldingListByUserId(Long userId) {
        return holdingRepository.findAllWithStockByUserId(userId);
    }

    public List<Holding> getAllHoldingsByPortfolios(List<Portfolio> portfolios) {
        return holdingRepository.findAllWithStockByPortfolioIn(portfolios);
    }

    public Holding create(Long userId, Stock stock, Portfolio portfolio, BigDecimal purchasePrice, int quantity) {
        Optional<Holding> holdingOptional = holdingRepository.findByPortfolioAndStock(portfolio, stock);
        if (holdingOptional.isPresent()) {
            Holding existHolding = holdingOptional.get();
            existHolding.addQuantityAndAvgPurchasePrice(quantity, purchasePrice);
            return existHolding;
        }

        trackedSymbolManager.saveTrackedSymbolIfNotExists(stock.getSymbol(), stock.getMarketCode());
        eventPublisher.publishEvent(new HoldingSubscriberEvent(userId, portfolio.getId(), stock.getSymbol(), CREATE));

        return holdingRepository.save(Holding.builder()
                .portfolio(portfolio)
                .stock(stock)
                .purchasePrice(purchasePrice)
                .quantity(quantity)
                .build());
    }

    public Holding update(Long holdingId, Portfolio portfolio, int quantity, BigDecimal purchasePrice) {
        return holdingRepository.findByIdAndPortfolio(holdingId, portfolio)
                .map(findHolding -> {
                    findHolding.updateQuantityAndPurchasePrice(quantity, purchasePrice);
                    return findHolding;
                })
                .orElseThrow(() -> new HoldingNotFoundException(HOLDING_NOT_FOUND));
    }

    public void deleteHoldingList(Portfolio portfolio) {
        List<Holding> holdingList = getHoldingList(portfolio.getId());
        holdingRepository.deleteAllByPortfolio(portfolio);

        holdingList.stream()
                .map(Holding::getStock)
                .forEach(trackedSymbolManager::deleteTrackedSymbolIfNotUse);
    }

    public void delete(Long holdingId, Portfolio portfolio, User user) {
        Holding holding = holdingRepository.findByIdAndPortfolio(holdingId, portfolio)
                .orElseThrow(() -> new HoldingNotFoundException(HOLDING_NOT_FOUND));

        holdingRepository.delete(holding);
        trackedSymbolManager.deleteTrackedSymbolIfNotUse(holding.getStock());
        eventPublisher.publishEvent(new HoldingSubscriberEvent(user.getId(), portfolio.getId(), holding.getStock().getSymbol(), DELETE));
    }
}
