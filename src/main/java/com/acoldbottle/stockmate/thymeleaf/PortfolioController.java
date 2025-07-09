package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioGetRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioWithProfitRes;
import com.acoldbottle.stockmate.api.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/stockmate/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private static final String ERR_MSG = "errorMessage";

    @GetMapping
    public String getPortfolios(Model model, @UserId Long userId) {
        List<PortfolioWithProfitRes> portfolioList = portfolioService.getPortfolioList(userId);
        model.addAttribute("portfolios", portfolioList);
        model.addAttribute("activePage", "portfolio");
        model.addAttribute("pageTitle", "StockMate - Portfolios");

        model.addAttribute("portfolioCreateReq", new PortfolioCreateReq());
        return "layout";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute @Valid PortfolioCreateReq portfolioCreateReq, BindingResult result, Model model, @UserId Long userId) {
        if (result.hasErrors()) {
            model.addAttribute("portfolioCreateReq", portfolioCreateReq);
            model.addAttribute(ERR_MSG, "포트폴리오 이름은 공백일 수 없습니다.");
            model.addAttribute("openCreateModal", true);
            model.addAttribute("portfolios", portfolioService.getPortfolioList(userId));
            return "layout"; // 모달을 포함한 전체 페이지 다시 렌더링
        }
        portfolioService.createPortfolio(userId, portfolioCreateReq);
        return "redirect:/stockmate/portfolios";
    }


}
