package com.acoldbottle.stockmate.api.portfolio.service;

import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioUpdateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioCreateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioUpdateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioWithProfitRes;
import com.acoldbottle.stockmate.api.profit.dto.PortfolioProfitDto;
import com.acoldbottle.stockmate.api.profit.service.ProfitCalculator;
import com.acoldbottle.stockmate.api.user.service.UserManager;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final UserManager userManager;
    private final PortfolioManager portfolioManager;
    private final ProfitCalculator profitCalculator;

    public List<PortfolioWithProfitRes> getPortfolioList(Long userId) {
        List<Portfolio> portfolioList = portfolioManager.getPortfolioList(userId);
        Map<Long, PortfolioProfitDto> portfolioProfitMap = profitCalculator.portfolioProfit(portfolioList);

        return portfolioList.stream()
                .map(portfolio -> {
                    PortfolioProfitDto portfolioProfit = portfolioProfitMap.get(portfolio.getId());
                    return PortfolioWithProfitRes.from(portfolio, portfolioProfit);
                })
                .sorted(Comparator.comparing(PortfolioWithProfitRes::getPortfolioCurrentValue))
                .toList();
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
}
