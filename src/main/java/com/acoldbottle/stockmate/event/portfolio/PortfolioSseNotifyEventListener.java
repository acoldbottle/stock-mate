package com.acoldbottle.stockmate.event.portfolio;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.api.holding.manager.HoldingManager;
import com.acoldbottle.stockmate.api.portfolio.manager.PortfolioManager;
import com.acoldbottle.stockmate.api.profit.dto.HoldingCurrentInfoDto;
import com.acoldbottle.stockmate.api.profit.dto.PortfolioProfitDto;
import com.acoldbottle.stockmate.api.profit.service.ProfitCalculator;
import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioSseService;
import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioSubscriberRegistry;
import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioUpdateDto;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PortfolioSseNotifyEventListener {

    private final PortfolioSseService portfolioSseService;
    private final ProfitCalculator profitCalculator;
    private final PortfolioSubscriberRegistry subscriberRegistry;
    private final PortfolioManager portfolioManager;
    private final HoldingManager holdingManager;
    private final CurrentPriceCacheService cacheService;

    @EventListener
    public void handlePriceUpdate(PortfolioSseNotifyEvent event) {
        String symbol = event.getSymbol();
        List<Long> subscribers = subscriberRegistry.getSubscribersBySymbol(symbol);
        if (subscribers.isEmpty()) {
            return;
        }

        Map<Long, List<PortfolioUpdateDto>> results = new HashMap<>();

        // 가격이 변한 주식종목을 들고있는 포트폴리오 리스트
        List<Portfolio> portfolioList = portfolioManager.getPortfolioListByPortfolioIds(subscribers);

        // 주식종목 + 현재시세 리스트
        List<Holding> holdings = holdingManager.getAllHoldingsByPortfolios(portfolioList);
        List<HoldingCurrentInfoDto> holdingCurrentInfoList = holdings.stream()
                .map(holding -> HoldingCurrentInfoDto.from(holding, cacheService.getCurrentPrice(holding.getStock().getSymbol())))
                .toList();

        // 포트폴리오 아이디로 그룹화
        Map<Long, List<HoldingCurrentInfoDto>> holdingCurrentInfoMap = holdingCurrentInfoList.stream()
                .collect(Collectors.groupingBy(HoldingCurrentInfoDto::getPortfolioId));

        // 포트폴리오 수익 계산 ->  유저아이디를 키로 map에 저장
        portfolioList
                .forEach(portfolio -> {
                    Long userId = portfolio.getUser().getId();
                    PortfolioProfitDto portfolioProfit = profitCalculator.portfolioProfit(holdingCurrentInfoMap.get(portfolio.getId()));
                    PortfolioUpdateDto updateDto = PortfolioUpdateDto.from(portfolio.getId(), portfolioProfit);
                    results.computeIfAbsent(userId, k -> new ArrayList<>()).add(updateDto);
                });

        portfolioSseService.notifyUpdatePortfolio(results);
    }
}
