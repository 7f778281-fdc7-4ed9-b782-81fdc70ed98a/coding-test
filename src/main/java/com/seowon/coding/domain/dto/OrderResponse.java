package com.seowon.coding.domain.dto;

import java.util.List;

public record OrderResponse(
        String customerName,
        String customerEmail,
        List<ProductResponse> productResponseList

) {
}
