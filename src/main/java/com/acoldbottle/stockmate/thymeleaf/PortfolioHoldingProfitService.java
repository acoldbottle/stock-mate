package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.api.profit.dto.ProfitDTO;
import com.acoldbottle.stockmate.api.profit.service.ProfitService;
import com.acoldbottle.stockmate.domain.holding.Holding;
import com.acoldbottle.stockmate.domain.holding.HoldingRepository;
import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.portfolio.PortfolioRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import com.acoldbottle.stockmate.exception.user.UserNotFoundException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 해당 포트폴리오의 자산가치, 수익률과 홀딩 목록을 화면에 보여주기 위한 서비스
 */
@Service
@RequiredArgsConstructor
public class PortfolioHoldingProfitService {

    private final ProfitService profitService;
    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    public PortfolioHoldingListDTO getHoldingList(Long userId, Long portfolioId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId, user)
                .orElseThrow(() -> new PortfolioNotFoundException(ErrorCode.PORTFOLIO_NOT_FOUND));
        List<Holding> holdings = holdingRepository.findAllWithStockByPortfolio(portfolio);
        String title = portfolio.getTitle();
        ProfitDTO profitDTO = profitService.calculateProfitInPortfolio(holdings);
        return PortfolioHoldingListDTO.from(title, profitDTO);
    }

//    @Getter
//    @Builder
//    public static class PortfolioHoldingListDTO {
//        private String portfolioTitle;
//        private BigDecimal portfolioCurrentValue;
//        private BigDecimal portfolioProfitAmount;
//        private BigDecimal portfolioProfitRate;
//        private List<ProfitDTO.HoldingProfitDTO> holdingList;
//
//        public static PortfolioHoldingListDTO from(String title, ProfitDTO profitDTO) {
//            return PortfolioHoldingListDTO.builder()
//                    .portfolioTitle(title)
//                    .portfolioCurrentValue(profitDTO.getPortfolioCurrentValue())
//                    .portfolioProfitAmount(profitDTO.getPortfolioProfitAmount())
//                    .portfolioProfitRate(profitDTO.getPortfolioProfitRate())
//                    .holdingList(profitDTO.getHoldingList())
//                    .build();
//        }
//    }
}
