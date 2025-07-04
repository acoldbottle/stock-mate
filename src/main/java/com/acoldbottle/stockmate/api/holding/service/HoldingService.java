package com.acoldbottle.stockmate.api.holding.service;

import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingCreateRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingWithProfitRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingUpdateRes;
import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import com.acoldbottle.stockmate.api.profit.service.ProfitService;
import com.acoldbottle.stockmate.api.trackedsymbol.service.TrackedSymbolService;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.holding.HoldingNotFoundException;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import com.acoldbottle.stockmate.exception.stock.StockNotFoundException;
import com.acoldbottle.stockmate.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.acoldbottle.stockmate.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final TrackedSymbolService trackedSymbolService;
    private final ProfitService profitService;

    public List<HoldingWithProfitRes> getHoldingListWithProfit(Long userId, Long portfolioId) {
        User user = getUser(userId);
        Portfolio portfolio = getPortfolio(portfolioId, user);
        List<Holding> holdingList = holdingRepository.findAllWithStockByPortfolio(portfolio);
        return profitService.calculateProfitInPortfolio(holdingList)
                .getHoldingList()
                .stream()
                .map(HoldingWithProfitRes::from)
                .toList();
    }

    @Transactional
    public HoldingCreateRes createHolding(Long userId, Long portfolioId, HoldingCreateReq holdingCreateReq) {
        User user = getUser(userId);
        Portfolio portfolio = getPortfolio(portfolioId, user);
        Stock stock = getStock(holdingCreateReq.getSymbol());

        Holding holding = holdingRepository.findByPortfolioAndStock(portfolio, stock)
                .map(existHolding -> {
                    existHolding.addQuantityAndAvgPurchasePrice(holdingCreateReq.getQuantity(), holdingCreateReq.getPurchasePrice());
                    return existHolding;
                })
                .orElseGet(() -> {
                    Holding newHolding = Holding.builder()
                            .portfolio(portfolio)
                            .stock(stock)
                            .quantity(holdingCreateReq.getQuantity())
                            .purchasePrice(holdingCreateReq.getPurchasePrice())
                            .build();
                    holdingRepository.save(newHolding);
                    return newHolding;
                });
        trackedSymbolService.saveTrackedSymbolIfNotExists(stock.getSymbol(), stock.getMarketCode());
        return HoldingCreateRes.from(holding);
    }

    @Transactional
    public HoldingUpdateRes updateHolding(Long userId, Long portfolioId, Long holdingId, HoldingUpdateReq holdingUpdateReq) {
        User user = getUser(userId);
        Portfolio portfolio = getPortfolio(portfolioId, user);
        Holding findHolding = getHolding(holdingId, portfolio);
        findHolding.updateQuantityAndPurchasePrice(holdingUpdateReq.getQuantity(), holdingUpdateReq.getPurchasePrice());

        return HoldingUpdateRes.from(findHolding);
    }

    @Transactional
    public void deleteHolding(Long userId, Long portfolioId, Long holdingId) {
        User user = getUser(userId);
        Portfolio portfolio = getPortfolio(portfolioId, user);
        Holding findHolding = getHolding(holdingId, portfolio);
        holdingRepository.delete(findHolding);
        trackedSymbolService.deleteTrackedSymbolIfNotUse(findHolding.getStock());
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

    private Holding getHolding(Long holdingId, Portfolio portfolio) {
        return holdingRepository.findByIdAndPortfolio(holdingId, portfolio)
                .orElseThrow(() -> new HoldingNotFoundException(HOLDING_NOT_FOUND));
    }
}
