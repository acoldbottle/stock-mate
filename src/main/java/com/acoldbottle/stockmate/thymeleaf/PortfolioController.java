package com.acoldbottle.stockmate.thymeleaf;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stockmate")
@RequiredArgsConstructor
public class PortfolioController {

    @GetMapping("/portfolios")
    public String getPortfolios() {
        return "portfolios";
    }
}
