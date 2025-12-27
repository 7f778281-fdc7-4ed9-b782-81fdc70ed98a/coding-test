package com.seowon.coding.dto;

import com.seowon.coding.domain.model.Order;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductRspDto {
    private String customerName;
    private String customerEmail;
    private List<Product> products;

    @Getter
    public static class Product {
        private Long productId;
        private Integer quantity;
    }

    public static ProductRspDto from(Order order) {
        return ProductRspDto.builder()
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .products()
                .build();
    }
}
