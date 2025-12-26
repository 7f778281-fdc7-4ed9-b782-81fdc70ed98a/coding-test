package com.seowon.coding.domain.model;

import com.seowon.coding.domain.enumuration.TaxVat;
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

    // TaxVat Enum 정의를 통해 VAT 값의 하드코딩 방지
    // 추후 지역 / 카테고리별 규칙이 발생하는 경우 Enum 값을 추가 / 수정하는 것으로 규칙 반영 수월
    public void priceChange(double percentage, boolean includeTax, TaxVat taxVat) {
        double base = this.price == null ? 0.0 : this.price.doubleValue();
        double changed = base + (base * (float) (percentage / 100.0));

        if(includeTax)
            changed = changed * taxVat.getVat();

        // BigDecimal 이해도 부족으로 해당 부분 해결하지 못했습니다.
        BigDecimal newPrice = BigDecimal.valueOf(changed).setScale(2, RoundingMode.HALF_UP);

        this.price = newPrice;
    }

    public BigDecimal priceMultiply(int qty) {
        return this.getPrice().multiply(BigDecimal.valueOf(qty));
    }
}