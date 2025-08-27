package com.acoldbottle.stockmate.domain.portfolio;

import com.acoldbottle.stockmate.domain.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query("SELECT p FROM Portfolio p WHERE p.user = :user ORDER BY p.id DESC")
    List<Portfolio> findAllByUser(User user);
    Optional<Portfolio> findByIdAndUser(Long id, User user);

    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId")
    List<Portfolio> findAllByUserId(Long userId);

    @Query("SELECT p FROM Portfolio p " +
            "JOIN FETCH p.user " +
            "WHERE p.id IN :portfolioIds")
    List<Portfolio> findAllWithUserByIds(List<Long> portfolioIds);
}
