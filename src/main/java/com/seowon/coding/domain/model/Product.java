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
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        stockQuantity -= quantity;
    }
    
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stockQuantity += quantity;
    }

    public void changePrice(double percentage, boolean includeTax) {
        // 잘못된 구현 예시: double 사용, 루프 내 개별 조회/저장, 하드코딩 세금/반올림 규칙

        BigDecimal base = this.getPrice() == null ? BigDecimal.ZERO : this.getPrice();
        base = base.setScale(2, RoundingMode.HALF_UP);
        BigDecimal changed = base.add(base.multiply(BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        if (includeTax) {
            changed = changed.add(changed.divide(BigDecimal.valueOf(10), RoundingMode.HALF_UP));
//            changed = changed * 1.1; // 하드코딩 VAT 10%, 지역/카테고리별 규칙 미반영

        }
        this.setPrice(changed);
    }

}