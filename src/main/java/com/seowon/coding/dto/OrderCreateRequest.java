package com.seowon.coding.dto;

import java.util.List;

public record OrderCreateRequest(
        String customerName,
        String customerEmail,
        List<ProductDto> products
        ) {
}
