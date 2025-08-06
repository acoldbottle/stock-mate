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
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId " +
            "ORDER BY h.purchasePrice * h.quantity DESC")
    List<Holding> findAllByPortfolioId(Long portfolioId);
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

    @Query("SELECT h.stock.symbol FROM Holding h WHERE h.portfolio.user.id = :userId")
    List<String> findSymbolsByUserId(Long userId);

    @Query("SELECT COUNT(h) > 0 FROM Holding h " +
            "WHERE h.portfolio.user.id = :userId " +
            "AND h.stock.symbol = :symbol")
    boolean existsByUserIdAndSymbol(Long userId, String symbol);
}
