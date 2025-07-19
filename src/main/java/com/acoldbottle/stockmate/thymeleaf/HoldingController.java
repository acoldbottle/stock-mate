package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.service.HoldingService;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioUpdateReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Controller
@RequestMapping("/stockmate/portfolios")
@RequiredArgsConstructor
public class HoldingController {

    private final HoldingService holdingService;
    private final PortfolioHoldingProfitService portfolioHoldingProfitService;

    @GetMapping("/{portfolioId}/stocks")
    public String getHoldings(@PathVariable Long portfolioId, Model model, @UserId Long userId) {
        PortfolioHoldingListDTO result = portfolioHoldingProfitService.getHoldingList(userId, portfolioId);
        setupHoldingList(portfolioId, model, result);

        return "layout";
    }

    @PostMapping("/{portfolioId}/stocks/{holdingId}/update")
    public String updateHolding(@ModelAttribute @Valid HoldingUpdateReq holdingUpdateReq,
                                BindingResult result,
                                Model model,
                                @UserId Long userId,
                                @PathVariable Long portfolioId,
                                @PathVariable Long holdingId) {
        PortfolioHoldingListDTO holdingList = portfolioHoldingProfitService.getHoldingList(userId, portfolioId);
        if (result.hasErrors()) {
            String[] fields = {"quantity", "purchasePrice"};
            for (String field : fields) {
                if (result.hasFieldErrors(field)) {
                    FieldError error = result.getFieldErrors(field).getFirst();
                    String errorMessage;
                    if (error.getCode().equals("typeMismatch")) {
                        errorMessage = "숫자만 입력해주세요. ex) 124.00 or 124";
                    } else {
                        errorMessage = error.getDefaultMessage();
                    }
                    model.addAttribute("errorMessage", errorMessage);
                    model.addAttribute("holdingId", holdingId);
                    model.addAttribute("openHoldingUpdateModal", true);
                    model.addAttribute("holdingUpdateReq", new HoldingUpdateReq());
                    setupHoldingList(portfolioId, model, holdingList);
                    return "layout";
                }
            }
        }
        holdingService.updateHolding(userId, portfolioId, holdingId, holdingUpdateReq);

        return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
    }


    @PostMapping("/{portfolioId}/stocks/{holdingId}/delete")
    public String deleteHolding(@PathVariable Long portfolioId,
                                @PathVariable Long holdingId,
                                @UserId Long userId) {
        holdingService.deleteHolding(userId, portfolioId, holdingId);
        return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
    }

    private void setupHoldingList(Long portfolioId, Model model, PortfolioHoldingListDTO result) {
        model.addAttribute("activeMenu", "portfolio");
        model.addAttribute("activePage", "holding");
        model.addAttribute("portfolioId", portfolioId);
        model.addAttribute("portfolioTitle", result.getPortfolioTitle());
        model.addAttribute("portfolioCurrentValue", result.getPortfolioCurrentValue());
        model.addAttribute("portfolioProfitAmount", result.getPortfolioProfitAmount());
        model.addAttribute("portfolioProfitRate", result.getPortfolioProfitRate());
        model.addAttribute("holdingList", result.getHoldingList());
    }
}
