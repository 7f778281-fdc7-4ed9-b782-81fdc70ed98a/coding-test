package com.seowon.coding.domain.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class CreateOrder {
    private String customerName;
    private String customerEmail;
    private List<Product> products;

    @Data
    @Builder
    public static class Product {
        private Long productId;
        private int quantity;
    }
    /*
     * Request body 예시:
     * {
     *   "customerName": "John Doe",
     *   "customerEmail": "john@example.com",
     *   "products": [
     *     {"productId": 1, "quantity": 2},
     *     {"productId": 3, "quantity": 1}
     *   ]
     * }
     */
}




