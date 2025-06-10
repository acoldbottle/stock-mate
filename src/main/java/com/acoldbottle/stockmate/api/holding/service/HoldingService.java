package com.acoldbottle.stockmate.api.holding.service;

import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingCreateRes;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import com.acoldbottle.stockmate.exception.stock.StockNotFoundException;
import com.acoldbottle.stockmate.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.acoldbottle.stockmate.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Transactional
    public HoldingCreateRes createHolding(Long userId, Long portfolioId, HoldingCreateReq holdingCreateReq) {
        User user = getUser(userId);
        Portfolio portfolio = getPortfolio(portfolioId, user);
        Stock stock = getStock(holdingCreateReq.getSymbol());

        Optional<Holding> holdingOptional = holdingRepository.findByPortfolioAndStock(portfolio, stock);
        Holding holding;
        if (holdingOptional.isPresent()) {
            Holding existHolding = holdingOptional.get();
            existHolding.addQuantityAndAvgPurchasePrice(holdingCreateReq.getQuantity(), holdingCreateReq.getPurchasePrice());
            holding = existHolding;
        } else {
            Holding newHolding = Holding.builder()
                    .portfolio(portfolio)
                    .stock(stock)
                    .quantity(holdingCreateReq.getQuantity())
                    .purchasePrice(holdingCreateReq.getPurchasePrice())
                    .build();
            holding = newHolding;
            holdingRepository.save(newHolding);
        }

        return HoldingCreateRes.from(holding);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private Portfolio getPortfolio(Long portfolioId, User user) {
        return portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new PortfolioNotFoundException(PORTFOLIO_NOT_FOUND));
    }

    private Stock getStock(String symbol) {
        return stockRepository.findById(symbol)
                .orElseThrow(() -> new StockNotFoundException(STOCK_NOT_FOUND));
    }
}
