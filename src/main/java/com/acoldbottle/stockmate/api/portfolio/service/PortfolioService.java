package com.acoldbottle.stockmate.api.portfolio.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.api.holding.service.HoldingManager;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioUpdateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioCreateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioUpdateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioWithProfitRes;
import com.acoldbottle.stockmate.api.profit.dto.HoldingCurrentInfoDto;
import com.acoldbottle.stockmate.api.profit.dto.PortfolioProfitDto;
import com.acoldbottle.stockmate.api.profit.service.ProfitCalculator;
import com.acoldbottle.stockmate.api.user.service.UserManager;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final UserManager userManager;
    private final PortfolioManager portfolioManager;
    private final HoldingManager holdingManager;
    private final CurrentPriceCacheService cacheService;
    private final ProfitCalculator profitCalculator;

    public List<PortfolioWithProfitRes> getPortfolioList(Long userId) {
        List<Portfolio> portfolioList = portfolioManager.getPortfolioList(userId);
        List<Holding> holdingList = holdingManager.getHoldingListByUserId(userId);
        Map<Long, List<HoldingCurrentInfoDto>> holdingListMap = getHoldingListGroupedByPortfolioId(holdingList);

        return getPortfolioListWithProfit(portfolioList, holdingListMap);
    }

    @Transactional
    public PortfolioCreateRes createPortfolio(Long userId, PortfolioCreateReq portfolioCreateReq) {
        User user = userManager.get(userId);

        Portfolio savedPortfolio = portfolioManager.create(user, portfolioCreateReq.getTitle());
        return PortfolioCreateRes.from(savedPortfolio);
    }

    @Transactional
    public PortfolioUpdateRes updatePortfolio(Long userId, Long portfolioId, PortfolioUpdateReq portfolioUpdateReq) {
        User user = userManager.get(userId);

        Portfolio findPortfolio = portfolioManager.get(portfolioId, user);
        findPortfolio.updatePortfolio(portfolioUpdateReq.getTitle());
        return PortfolioUpdateRes.from(portfolioId, portfolioUpdateReq.getTitle());
    }

    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        User user = userManager.get(userId);

        Portfolio findPortfolio = portfolioManager.get(portfolioId, user);
        portfolioManager.delete(findPortfolio);
    }

    private Map<Long, List<HoldingCurrentInfoDto>> getHoldingListGroupedByPortfolioId(List<Holding> holdingList) {
        return holdingList.stream()
                .map(holding -> {
                    CurrentPriceDTO currentPrice = cacheService.getCurrentPrice(holding.getStock().getSymbol());
                    return HoldingCurrentInfoDto.from(holding, currentPrice);
                })
                .collect(Collectors.groupingBy(HoldingCurrentInfoDto::getPortfolioId));
    }

    private List<PortfolioWithProfitRes> getPortfolioListWithProfit(List<Portfolio> portfolioList, Map<Long, List<HoldingCurrentInfoDto>> holdingListMap) {
        return portfolioList.stream()
                .map(portfolio -> {
                    List<HoldingCurrentInfoDto> holdingList = holdingListMap.getOrDefault(portfolio.getId(), Collections.emptyList());
                    PortfolioProfitDto portfolioProfit = profitCalculator.portfolioProfit(holdingList);
                    return PortfolioWithProfitRes.from(portfolio, portfolioProfit);
                })
                .sorted(Comparator.comparing(PortfolioWithProfitRes::getPortfolioCurrentValue))
                .toList();
    }
}
