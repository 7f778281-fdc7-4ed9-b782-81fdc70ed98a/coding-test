package com.seowon.coding.domain.dto.order.req;

public record OrderProduct(
        long productId,
        int quantity
) {
}
