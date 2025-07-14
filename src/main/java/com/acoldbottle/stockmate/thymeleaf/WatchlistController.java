package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import com.acoldbottle.stockmate.api.stock.service.StockService;
import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemGetRes;
import com.acoldbottle.stockmate.api.watchlist.service.WatchlistService;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemAlreadyExistsException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stockmate/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final StockService stockService;

    @GetMapping
    public String getWatchlist(Model model, @UserId Long userId) {
        List<WatchItemGetRes> watchlist = watchlistService.getWatchlist(userId);
        setupWatchlist(model, watchlist);
        return "layout";
    }

    @PostMapping("/create")
    public String createWatchItem(@ModelAttribute @Valid WatchItemCreateReq watchItemCreateReq, BindingResult bindingResult,
                                  Model model, @UserId Long userId) {
        List<WatchItemGetRes> watchlist = watchlistService.getWatchlist(userId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "유효하지 않은 관심종목 심볼입니다.");
            model.addAttribute("openSearchModal", true);
            setupWatchlist(model, watchlist);
            return "layout";
        }
        try {
            watchlistService.createWatchItem(userId, watchItemCreateReq);
        } catch (WatchItemAlreadyExistsException e) {
            model.addAttribute("errorMessage", "이미 관심종목에 등록 된 주식입니다.");
            model.addAttribute("openSearchModal", true);
            setupWatchlist(model, watchlist);
            return "layout";
        }
        return "redirect:/stockmate/watchlist";
    }

    @PostMapping("/{watchItemId}/delete")
    public String deleteWatchItem(@UserId Long userId, @PathVariable Long watchItemId) {
        watchlistService.deleteWatchItem(userId, watchItemId);
        return "redirect:/stockmate/watchlist";
    }

    @GetMapping("/search")
    public String searchStocks(@RequestParam String keyword, @UserId Long userId,Model model) {
        if (keyword == null || keyword.isBlank()) {
            model.addAttribute("errorMessage", "검색어를 입력해주세요.");
            model.addAttribute("searchResults", null);
        } else {
            List<StockSearchRes> searchResults = stockService.searchByKeyword(keyword);
            model.addAttribute("searchResults", searchResults);
        }
        List<WatchItemGetRes> watchlist = watchlistService.getWatchlist(userId);
        model.addAttribute("openSearchModal", true);
        setupWatchlist(model, watchlist);
        return "layout";
    }

    private void setupWatchlist(Model model, List<WatchItemGetRes> watchlist) {
        model.addAttribute("watchlist", watchlist);
        model.addAttribute("activePage", "watchlist");
        model.addAttribute("activeMenu", "watchlist");
        model.addAttribute("pageTitle", "StockMate - Watchlist");
        model.addAttribute("watchItemCreateReq", new WatchItemCreateReq());
    }
}
