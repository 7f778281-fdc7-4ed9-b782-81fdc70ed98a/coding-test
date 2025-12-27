package com.seowon.coding.controller.dto;

import com.seowon.coding.service.OrderProduct;

import java.util.List;

public record OrderCreateRequestDto(
        String customerName,
        String customerEmail,
        List<OrderProduct> products,
        String couponCode

) {
}
