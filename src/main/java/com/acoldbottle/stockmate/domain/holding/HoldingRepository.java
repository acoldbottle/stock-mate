package com.acoldbottle.stockmate.domain.holding;

import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    Optional<Holding> findByPortfolioAndStock(Portfolio portfolio, Stock stock);
    Optional<Holding> findByIdAndPortfolio(Long id, Portfolio portfolio);

    boolean existsByStock_symbol(String symbol);
}
