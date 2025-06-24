package com.acoldbottle.stockmate.domain.watchitem;

import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchItemRepository extends JpaRepository<WatchItem, Long> {

    boolean existsByUserAndStock(User user, Stock stock);
    Optional<WatchItem> findByIdAndUser(Long id, User user);

    boolean existsByStock(Stock stock);
}
