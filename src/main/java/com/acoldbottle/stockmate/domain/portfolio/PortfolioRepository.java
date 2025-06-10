package com.acoldbottle.stockmate.domain.portfolio;

import com.acoldbottle.stockmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query("SELECT p FROM Portfolio p WHERE p.user = :user ORDER BY p.id DESC")
    List<Portfolio> findAllByUser(User user);

    Optional<Portfolio> findByIdAndUser(Long id, User user);
}
