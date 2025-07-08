package com.acoldbottle.stockmate.domain.holding;

import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    Optional<Holding> findByPortfolioAndStock(Portfolio portfolio, Stock stock);
    Optional<Holding> findByIdAndPortfolio(Long id, Portfolio portfolio);
    boolean existsByStock(Stock stock);
    @Query("SELECT h FROM Holding h WHERE h.portfolio = :portfolio " +
            "ORDER BY h.purchasePrice * h.quantity DESC")
    List<Holding> findAllByPortfolio(Portfolio portfolio);
    @Modifying
    @Query("DELETE FROM Holding h WHERE h.portfolio = :portfolio")
    void deleteAllByPortfolio(Portfolio portfolio);
    @Query("SELECT h FROM Holding h " +
            "JOIN FETCH h.stock " +
            "WHERE h.portfolio = :portfolio " +
            "ORDER BY h.purchasePrice * h.quantity DESC")
    List<Holding> findAllWithStockByPortfolio(Portfolio portfolio);
    @Query("SELECT h FROM Holding h " +
            "JOIN FETCH h.stock " +
            "WHERE h.portfolio IN :portfolios")
    List<Holding> findAllWithStockByPortfolioIn(List<Portfolio> portfolios);
}
