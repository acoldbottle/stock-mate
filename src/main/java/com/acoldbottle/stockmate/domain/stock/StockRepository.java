package com.acoldbottle.stockmate.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {

    @Query(value =
    """
    SELECT * FROM stocks
    WHERE LOWER(symbol) LIKE LOWER(CONCAT(:keyword, '%'))
       OR LOWER(eng_name) LIKE LOWER(CONCAT(:keyword, '%'))
       OR LOWER(kor_name) LIKE LOWER(CONCAT(:keyword, '%'))
    ORDER BY
        CASE
            WHEN LOWER(symbol) = LOWER(:keyword) THEN 0
            WHEN LOWER(eng_name) = LOWER(:keyword) THEN 1
            WHEN LOWER(kor_name) = LOWER(:keyword) THEN 1
            ELSE 2
        END,
        symbol
    LIMIT 10
    """, nativeQuery = true)
    List<Stock> searchByKeyword(String keyword);
}
