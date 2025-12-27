package com.seowon.coding.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private int stockQuantity;
    
    private String category;
    
    // Business logic
    public boolean isInStock() {
        return stockQuantity > 0;
    }
    
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.getStockQuantity() < quantity) {
            throw new IllegalStateException("insufficient stock for product " + this.id);
        }
        stockQuantity -= quantity;
    }
    
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stockQuantity += quantity;
    }

    /**
     * 가격 변경 (퍼센트 기반)
     */
    public void applyPriceChange(BigDecimal percentage, TaxPolicy taxPolicy) {
        if (this.price == null) {
            throw new IllegalArgumentException("Cannot change price: current price is null for product " + this.id);
        }
        
        if (percentage == null) {
            throw new IllegalArgumentException("Percentage cannot be null");
        }
        
        // BigDecimal로 정확한 계산
        BigDecimal multiplier = BigDecimal.ONE.add(percentage.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
        BigDecimal newPrice = this.price.multiply(multiplier);
        
        // 세금 적용
        if (taxPolicy != null) {
            newPrice = taxPolicy.applyTax(newPrice, this);
        }
        
        // 최종 가격: 소수점 2자리까지 반올림
        newPrice = newPrice.setScale(2, RoundingMode.HALF_UP);
        
        // 가격이 음수가 되는 것 방지
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative after change");
        }
        
        this.price = newPrice;
    }

}