package com.acoldbottle.stockmate.api.holding.service;

import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingCreateRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingUpdateRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingWithProfitRes;
import com.acoldbottle.stockmate.api.portfolio.service.PortfolioManager;
import com.acoldbottle.stockmate.api.profit.dto.HoldingProfitDto;
import com.acoldbottle.stockmate.api.profit.service.ProfitCalculator;
import com.acoldbottle.stockmate.api.stock.service.StockManager;
import com.acoldbottle.stockmate.api.user.service.UserManager;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HoldingService {

    private final UserManager userManager;
    private final PortfolioManager portfolioManager;
    private final HoldingManager holdingManager;
    private final StockManager stockManager;
    private final ProfitCalculator profitCalculator;

    public List<HoldingWithProfitRes> getHoldingListWithProfit(Long userId, Long portfolioId) {
        User user = userManager.get(userId);
        Portfolio portfolio = portfolioManager.get(portfolioId, user);

        List<Holding> holdingList = holdingManager.getHoldingListIn(portfolio);
        Map<Long, HoldingProfitDto> holdingProfitMap = profitCalculator.holdingListProfit(holdingList);

        return holdingList.stream()
                .map(holding -> {
                    HoldingProfitDto holdingProfit = holdingProfitMap.get(holding.getId());
                    return HoldingWithProfitRes.from(holding, holdingProfit);
                })
                .toList();
    }

    @Transactional
    public HoldingCreateRes createHolding(Long userId, Long portfolioId, HoldingCreateReq createReq) {
        User user = userManager.get(userId);
        Portfolio portfolio = portfolioManager.get(portfolioId, user);
        Stock stock = stockManager.get(createReq.getSymbol());

        Holding holding = holdingManager.create(userId, stock, portfolio, createReq.getPurchasePrice(), createReq.getQuantity());
        return HoldingCreateRes.from(holding);
    }

    @Transactional
    public HoldingUpdateRes updateHolding(Long userId, Long portfolioId, Long holdingId, HoldingUpdateReq updateReq) {
        User user = userManager.get(userId);
        Portfolio portfolio = portfolioManager.get(portfolioId, user);

        Holding updatedHolding = holdingManager.update(holdingId, portfolio, updateReq.getQuantity(), updateReq.getPurchasePrice());
        return HoldingUpdateRes.from(updatedHolding);
    }

    @Transactional
    public void deleteHolding(Long userId, Long portfolioId, Long holdingId) {
        User user = userManager.get(userId);
        Portfolio portfolio = portfolioManager.get(portfolioId, user);

        holdingManager.delete(holdingId, portfolio, user);
    }
}
