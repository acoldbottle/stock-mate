package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.service.HoldingService;
import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import com.acoldbottle.stockmate.api.stock.service.StockService;
import com.acoldbottle.stockmate.exception.holding.HoldingNotFoundException;
import com.acoldbottle.stockmate.exception.portfolio.PortfolioNotFoundException;
import com.acoldbottle.stockmate.exception.stock.StockNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/stockmate/portfolios")
@RequiredArgsConstructor
public class HoldingController {

    private final HoldingService holdingService;
    private final StockService stockService;
    private final PortfolioHoldingProfitService portfolioHoldingProfitService;

    @GetMapping("/{portfolioId}/stocks")
    public String getHoldings(@PathVariable Long portfolioId, Model model, @UserId Long userId) {
        try {
            PortfolioHoldingListDTO result = portfolioHoldingProfitService.getHoldingList(userId, portfolioId);
            setupHoldingList(portfolioId, model, result);
        } catch (PortfolioNotFoundException e) {
            return "redirect:/stockmate/portfolios";
        }

        return "layout";
    }

    @PostMapping("/{portfolioId}/stocks/create")
    public String createHoldings(@PathVariable Long portfolioId,
                                 @ModelAttribute @Valid HoldingCreateReq holdingCreateReq,
                                 BindingResult result,
                                 Model model,
                                 @UserId Long userId) {
        PortfolioHoldingListDTO holdingList = portfolioHoldingProfitService.getHoldingList(userId, portfolioId);
        try {
            if (result.hasErrors()) {
                String[] fields = {"symbol", "quantity", "purchasePrice"};
                for (String field : fields) {
                    if (result.hasFieldErrors(field)) {
                        FieldError error = result.getFieldErrors(field).getFirst();
                        String errorMessage;
                        if (error.getCode().equals("typeMismatch")) {
                            errorMessage = "숫자만 입력해주세요. ex) 124.00 or 124";
                        } else {
                            errorMessage = error.getDefaultMessage();
                        }
                        List<StockSearchRes> searchResults = stockService.searchByKeyword(holdingCreateReq.getSymbol());
                        model.addAttribute("searchResults", searchResults);
                        model.addAttribute("errorMessageCreate", errorMessage);
                        model.addAttribute("openHoldingSearchModal", true);
                        model.addAttribute("openHoldingCreateModal", true);
                        model.addAttribute("symbol", holdingCreateReq.getSymbol());
                        model.addAttribute("holdingCreateReq", new HoldingCreateReq());
                        setupHoldingList(portfolioId, model, holdingList);
                        return "layout";
                    }
                }
            }
            holdingService.createHolding(userId, portfolioId, holdingCreateReq);
        } catch (StockNotFoundException e) {
            List<StockSearchRes> searchResults = stockService.searchByKeyword(holdingCreateReq.getSymbol());
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("openHoldingSearchModal", true);
            model.addAttribute("holdingCreateReq", new HoldingCreateReq());
            setupHoldingList(portfolioId, model, holdingList);
            return "layout";
        } catch (PortfolioNotFoundException e) {
            return "redirect:/stockmate/portfolios";
        }

        return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
    }

    @PostMapping("/{portfolioId}/stocks/{holdingId}/update")
    public String updateHolding(@ModelAttribute @Valid HoldingUpdateReq holdingUpdateReq,
                                BindingResult result,
                                Model model,
                                @UserId Long userId,
                                @PathVariable Long portfolioId,
                                @PathVariable Long holdingId) {
        PortfolioHoldingListDTO holdingList = portfolioHoldingProfitService.getHoldingList(userId, portfolioId);
        try {
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
        } catch (StockNotFoundException | HoldingNotFoundException e) {
            return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
        } catch (PortfolioNotFoundException e) {
            return "redirect:/stockmate/portfolios";
        }

        return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
    }


    @PostMapping("/{portfolioId}/stocks/{holdingId}/delete")
    public String deleteHolding(@PathVariable Long portfolioId,
                                @PathVariable Long holdingId,
                                @UserId Long userId) {
        try {
            holdingService.deleteHolding(userId, portfolioId, holdingId);
        } catch (HoldingNotFoundException e) {
            return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
        } catch (PortfolioNotFoundException e) {
            return "redirect:/stockmate/portfolios";
        }
        return "redirect:/stockmate/portfolios/" + portfolioId + "/stocks";
    }

    @GetMapping("/{portfolioId}/stocks/search")
    public String searchStocks(@RequestParam String keyword,
                               @UserId Long userId,
                               @PathVariable Long portfolioId,
                               Model model) {
        try {
            if (keyword == null || keyword.isBlank()) {
                model.addAttribute("errorMessage", "검색어를 입력해주세요.");
                model.addAttribute("searchResults", null);
            } else {
                List<StockSearchRes> searchResults = stockService.searchByKeyword(keyword);
                model.addAttribute("searchResults", searchResults);
            }
            PortfolioHoldingListDTO holdingList = portfolioHoldingProfitService.getHoldingList(userId, portfolioId);
            model.addAttribute("openHoldingSearchModal", true);
            setupHoldingList(portfolioId, model, holdingList);
        } catch (PortfolioNotFoundException e) {
            return "redirect:/stockmate/portfolios";
        }
        return "layout";
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
