package com.acoldbottle.stockmate.domain.holding;

import com.acoldbottle.stockmate.domain.portfolio.Portfolio;
import com.acoldbottle.stockmate.domain.stock.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Table(name = "holdings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holding {

    @Id
    @Column(name = "holding_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol", nullable = false)
    private Stock stock;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Builder
    public Holding(Portfolio portfolio, Stock stock, int quantity, BigDecimal purchasePrice) {
        this.portfolio = portfolio;
        this.stock = stock;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public void addQuantityAndAvgPurchasePrice(int newQuantity, BigDecimal newPurchasePrice) {
        int totalQuantity = this.quantity + newQuantity;

        BigDecimal oldTotalCost = this.purchasePrice.multiply(BigDecimal.valueOf(this.quantity));
        BigDecimal newTotalCost = newPurchasePrice.multiply(BigDecimal.valueOf(newQuantity));
        BigDecimal totalCost = oldTotalCost.add(newTotalCost);
        BigDecimal avgPurchasePrice = totalCost.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);

        this.quantity = totalQuantity;
        this.purchasePrice = avgPurchasePrice;
    }
}
