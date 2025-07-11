package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemGetRes;
import com.acoldbottle.stockmate.api.watchlist.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stockmate/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public String getWatchlist(Model model, @UserId Long userId) {
        List<WatchItemGetRes> watchlist = watchlistService.getWatchlist(userId);
        model.addAttribute("watchlist", watchlist);
        model.addAttribute("activePage", "watchlist");
        model.addAttribute("pageTitle", "StockMate - Watchlist");
        model.addAttribute("watchItemCreateReq", new WatchItemCreateReq());
        return "layout";
    }

    @PostMapping("/{watchItemId}/delete")
    public String deleteWatchItem(@UserId Long userId, @PathVariable Long watchItemId) {
        watchlistService.deleteWatchItem(userId, watchItemId);
        return "redirect:/stockmate/watchlist";
    }
}
