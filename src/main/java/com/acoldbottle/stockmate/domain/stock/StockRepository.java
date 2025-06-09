package com.acoldbottle.stockmate.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, String> {

    @Query("SELECT s FROM Stock s " +
            "WHERE LOWER(s.symbol) LIKE LOWER(CONCAT(:keyword, '%')) " +
            "OR LOWER(s.engName) LIKE LOWER(CONCAT(:keyword, '%')) " +
            "OR LOWER(s.korName) LIKE LOWER(CONCAT(:keyword, '%'))")
    List<Stock> searchByKeyword(String keyword);
}
