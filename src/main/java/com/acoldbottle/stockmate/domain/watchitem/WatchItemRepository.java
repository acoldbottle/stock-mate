package com.acoldbottle.stockmate.domain.watchitem;

import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchItemRepository extends JpaRepository<WatchItem, Long> {

    boolean existsByUserAndStock(User user, Stock stock);
}
