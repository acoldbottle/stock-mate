package com.acoldbottle.stockmate.domain.watchitem;

import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WatchItemRepository extends JpaRepository<WatchItem, Long> {

    @Query("SELECT w FROM WatchItem w WHERE w.user = :user " +
            "ORDER BY w.id DESC")
    List<WatchItem> findAllByUser(User user);
    boolean existsByUserAndStock(User user, Stock stock);
    Optional<WatchItem> findByIdAndUser(Long id, User user);
    boolean existsByStock(Stock stock);
}
