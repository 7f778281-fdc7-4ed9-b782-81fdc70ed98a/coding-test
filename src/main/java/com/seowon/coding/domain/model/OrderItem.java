package com.seowon.coding.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private BigDecimal price; // Price at the time of order

    @Builder
    public OrderItem(
        Order order,
        Product product,
        int quantity,
        BigDecimal price
    ) {

    }

    public static OrderItem of(
        Order order,
        Product product,
        int quantity,
        BigDecimal price
    ) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }

        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("insufficient stock for product " + product.getId());
        }

        return OrderItem.builder()
            .order(order)
            .product(product)
            .quantity(quantity)
            .price(price)
            .build();
    }

    // Business logic
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}