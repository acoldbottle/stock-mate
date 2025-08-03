package com.acoldbottle.stockmate.domain.trackedsymbol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TrackedSymbolRepository extends JpaRepository<TrackedSymbol, String> {

    @Modifying
    @Query(value = "DELETE FROM tracked_symbols " +
            "WHERE symbol = :symbol " +
            "  AND NOT EXISTS (SELECT 1 FROM holdings WHERE symbol = :symbol) " +
            "  AND NOT EXISTS (SELECT 1 FROM watchlist WHERE symbol = :symbol)", nativeQuery = true)
    void deleteTrackedSymbolIfNotExists(String symbol);
}
