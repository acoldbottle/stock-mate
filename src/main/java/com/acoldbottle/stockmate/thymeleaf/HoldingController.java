package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.holding.service.HoldingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stockmate/portfolios")
@RequiredArgsConstructor
public class HoldingController {

    private final HoldingService holdingService;
    private final PortfolioHoldingProfitService portfolioHoldingProfitService;

    @GetMapping("/{portfolioId}/stocks")
    public String getHoldings(@PathVariable Long portfolioId, Model model, @UserId Long userId) {
        PortfolioHoldingListDTO result = portfolioHoldingProfitService.getHoldingList(userId, portfolioId);

        model.addAttribute("activeMenu", "portfolio");
        model.addAttribute("activePage", "holding");
        model.addAttribute("portfolioId", portfolioId);
        model.addAttribute("portfolioTitle", result.getPortfolioTitle());
        model.addAttribute("portfolioCurrentValue", result.getPortfolioCurrentValue());
        model.addAttribute("portfolioProfitAmount", result.getPortfolioProfitAmount());
        model.addAttribute("portfolioProfitRate", result.getPortfolioProfitRate());
        model.addAttribute("holdingList", result.getHoldingList());

        return "layout";
    }

    @PostMapping("/{portfolioId}/stocks/{holdingId}/delete")
    public String deleteHolding(@PathVariable Long portfolioId,
                                @PathVariable Long holdingId,
                                @UserId Long userId) {
        holdingService.deleteHolding(userId, portfolioId, holdingId);
        return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
    }
}
