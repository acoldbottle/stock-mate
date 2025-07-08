package com.acoldbottle.stockmate.api.portfolio.service;

import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioUpdateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioCreateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioUpdateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioWithProfitRes;
import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import com.acoldbottle.stockmate.api.profit.service.ProfitService;
import com.acoldbottle.stockmate.api.trackedsymbol.service.TrackedSymbolService;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import com.acoldbottle.stockmate.exception.user.UserNotFoundException;
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

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final HoldingRepository holdingRepository;
    private final ProfitService profitService;
    private final TrackedSymbolService trackedSymbolService;

    public List<PortfolioWithProfitRes> getPortfolioList(Long userId) {
        User user = getUser(userId);
        List<Portfolio> portfolios = portfolioRepository.findAllByUser(user);
        Map<Long, List<Holding>> holdingsByPortfolioId = holdingRepository.findAllWithStockByPortfolioIn(portfolios)
                .stream()
                .collect(Collectors.groupingBy(h -> h.getPortfolio().getId()));

        return portfolios.stream()
                .map(portfolio -> {
                    List<Holding> holdings = holdingsByPortfolioId.getOrDefault(portfolio.getId(), Collections.emptyList());
                    ProfitDTO profitDTO = profitService.calculateProfitInPortfolio(holdings);
                    return PortfolioWithProfitRes.from(portfolio, profitDTO);
                })
                .sorted(Comparator.comparing(PortfolioWithProfitRes::getPortfolioCurrentValue).reversed())
                .toList();
    }

    @Transactional
    public PortfolioCreateRes createPortfolio(Long userId, PortfolioCreateReq portfolioCreateReq) {
        User user = getUser(userId);
        String title = portfolioCreateReq.getTitle();
        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .title(title)
                .build();
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return PortfolioCreateRes.from(savedPortfolio);
    }

    @Transactional
    public PortfolioUpdateRes updatePortfolio(Long userId, Long portfolioId, PortfolioUpdateReq portfolioUpdateReq) {
        User user = getUser(userId);
        Portfolio findPortfolio = getPortfolio(portfolioId, user);
        findPortfolio.updatePortfolio(portfolioUpdateReq.getTitle());
        return PortfolioUpdateRes.from(portfolioId, portfolioUpdateReq.getTitle());
    }

    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        User user = getUser(userId);
        Portfolio findPortfolio = getPortfolio(portfolioId, user);
        List<Holding> holdings = holdingRepository.findAllByPortfolio(findPortfolio);
        holdingRepository.deleteAllByPortfolio(findPortfolio);
        holdings.stream()
                        .map(Holding::getStock)
                                .forEach(trackedSymbolService::deleteTrackedSymbolIfNotUse);
        portfolioRepository.delete(findPortfolio);
    }

//    public PortfolioWithProfitRes getPortfolioWithProfit(Long userId, Long portfolioId) {
//        User user = getUser(userId);
//        Portfolio portfolio = getPortfolio(portfolioId, user);
//        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolio(portfolio);
//        ProfitDTO profitDTO = profitService.calculateProfitInPortfolio(holdings);
//        return PortfolioWithProfitRes.from(portfolio,profitDTO);
//    }

    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Portfolio getPortfolio(Long portfolioId, User user) {
        return portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new PortfolioNotFoundException(ErrorCode.PORTFOLIO_NOT_FOUND));
    }
}
