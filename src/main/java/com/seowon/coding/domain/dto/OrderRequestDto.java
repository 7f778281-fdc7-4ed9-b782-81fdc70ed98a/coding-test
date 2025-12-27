package com.seowon.coding.domain.dto;

import com.seowon.coding.domain.model.Product;

import java.util.List;

public record OrderRequestDto(
        String customerName,
        String customerEmail,
        List<Product> products
        ) {
}
