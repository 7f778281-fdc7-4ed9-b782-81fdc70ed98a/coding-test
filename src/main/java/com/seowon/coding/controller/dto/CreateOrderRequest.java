package com.seowon.coding.controller.dto;

import jakarta.validation.constraints.Email;

import java.util.List;

public record CreateOrderRequest(
    String customerName,
    @Email
    String customerEmail,
    List<ProductOrderRequest> products
) {
}
