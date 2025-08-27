package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingWithProfitRes;
import com.acoldbottle.stockmate.api.holding.manager.HoldingManager;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioWithProfitRes;
import com.acoldbottle.stockmate.api.portfolio.manager.PortfolioManager;
import com.acoldbottle.stockmate.api.profit.dto.HoldingCurrentInfoDto;
import com.acoldbottle.stockmate.api.profit.dto.HoldingProfitDto;
import com.acoldbottle.stockmate.api.profit.dto.PortfolioProfitDto;
import com.acoldbottle.stockmate.api.profit.service.ProfitCalculator;
import com.acoldbottle.stockmate.api.user.manager.UserManager;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 해당 포트폴리오의 자산가치, 수익률과 홀딩 목록을 화면에 함께 보여주기 위한 서비스
 */
@Service
@RequiredArgsConstructor
public class PortfolioHoldingProfitService {

    private final UserManager userManager;
    private final HoldingManager holdingManager;
    private final PortfolioManager portfolioManager;
    private final ProfitCalculator profitCalculator;
    private final CurrentPriceCacheService cacheService;

    @Transactional(readOnly = true)
    public PortfolioHoldingListDTO getPortfolioWithHoldingList(Long userId, Long portfolioId) {
        User user = userManager.get(userId);
        Portfolio portfolio = portfolioManager.get(portfolioId, user);
        List<Holding> holdingList = holdingManager.getHoldingList(portfolio.getId());

        List<HoldingCurrentInfoDto> holdingCurrentInfoList = holdingList.stream()
                .map(holding -> {
                    CurrentPriceDTO currentPrice = cacheService.getCurrentPrice(holding.getStock().getSymbol());
                    return HoldingCurrentInfoDto.from(holding, currentPrice);
                })
                .toList();

        PortfolioProfitDto portfolioProfit = profitCalculator.portfolioProfit(holdingCurrentInfoList);
        PortfolioWithProfitRes portfolioWithProfitRes = PortfolioWithProfitRes.from(portfolio, portfolioProfit);

        Map<Long, HoldingProfitDto> holdingProfitMap = profitCalculator.holdingListProfit(holdingCurrentInfoList);
        List<HoldingWithProfitRes> holdingWithProfitResList = holdingList.stream()
                .map(holding -> {
                    HoldingProfitDto holdingProfit = holdingProfitMap.get(holding.getId());
                    return HoldingWithProfitRes.from(holding, holdingProfit);
                })
                .toList();

        return PortfolioHoldingListDTO.builder()
                .portfolioInfo(portfolioWithProfitRes)
                .holdingList(holdingWithProfitResList)
                .build();
    }
}
