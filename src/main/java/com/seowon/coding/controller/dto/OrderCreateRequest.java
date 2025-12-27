package com.seowon.coding.controller.dto;

import com.seowon.coding.service.OrderProduct;

import java.util.List;

public record OrderCreateRequest(
        String customerName,
        String customerEmail,
        List<OrderProduct> products
) {
}
