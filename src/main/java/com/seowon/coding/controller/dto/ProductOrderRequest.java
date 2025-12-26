package com.seowon.coding.controller.dto;

import jakarta.validation.constraints.Min;

public record ProductOrderRequest(
        Long productId,
        @Min(0)
        Integer quantity
) {
}
