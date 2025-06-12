package com.acoldbottle.stockmate.domain.trackedsymbol;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackedSymbolRepository extends JpaRepository<TrackedSymbol, String> {
}
