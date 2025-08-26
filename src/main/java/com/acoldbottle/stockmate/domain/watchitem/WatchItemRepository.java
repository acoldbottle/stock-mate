package com.acoldbottle.stockmate.domain.watchitem;

import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WatchItemRepository extends JpaRepository<WatchItem, Long> {

    @Query("SELECT w FROM WatchItem w " +
            "JOIN FETCH w.stock " +
            "WHERE w.user.id = :userId " +
            "ORDER BY w.id DESC")
    List<WatchItem> findAllWithStockByUserId(Long userId);
    boolean existsByUserAndStock(User user, Stock stock);

    @Query("SELECT w FROM WatchItem w " +
            "JOIN FETCH w.stock " +
            "WHERE w.user.id = :userId" +
            "AND w,id = :id")
    Optional<WatchItem> findByIdAndUser(Long id, Long userId);

    @Query("SELECT w.stock.symbol FROM WatchItem w " +
            "WHERE w.user.id = :userId")
    List<String> findSymbolsByUserId(Long userId);

    @Query("SELECT COUNT(w) > 0 FROM WatchItem w " +
            "WHERE w.user.id = :userId " +
            "AND w.stock.symbol = :symbol")
    boolean existsByUserIdAndSymbol(Long userId, String symbol);
}
