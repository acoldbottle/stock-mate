package com.acoldbottle.stockmate.domain.trackedsymbol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tracked_symbols")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackedSymbol {

    @Id
    private String symbol;

    @Column(name = "market_code")
    private String marketCode;

    @Builder
    public TrackedSymbol(String symbol, String marketCode) {
        this.symbol = symbol;
        this.marketCode = marketCode;
    }
}
