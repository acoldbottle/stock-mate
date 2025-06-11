package com.acoldbottle.stockmate.domain.symbol;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "symbols")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Symbol {

    @Id
    private String symbol;

    @Builder
    public Symbol(String symbol) {
        this.symbol = symbol;
    }
}
