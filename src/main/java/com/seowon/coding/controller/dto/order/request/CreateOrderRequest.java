package com.seowon.coding.controller.dto.order.request;

import java.util.List;

public record CreateOrderRequest(
    String customerName,
    String customerEmail,
    List<ProductRequest> products
) {

    public record ProductRequest(
        Long productId,
        Integer quantity
    ) {

    }

}
