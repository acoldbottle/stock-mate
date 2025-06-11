package com.acoldbottle.stockmate.domain.symbol;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<Symbol, String> {
}
