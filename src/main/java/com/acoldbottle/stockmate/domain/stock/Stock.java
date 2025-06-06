package com.acoldbottle.stockmate.domain.stock;

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
@Table(name = "stocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    private String symbol;

    @Column(name = "kor_name")
    private String korName;

    @Column(name = "eng_name")
    private String engName;

    @Column(name = "market_code")
    private String marketCode;

    @Builder
    public Stock(String symbol, String korName, String engName, String marketCode) {
        this.symbol = symbol;
        this.korName = korName;
        this.engName = engName;
        this.marketCode = marketCode;
    }
}
