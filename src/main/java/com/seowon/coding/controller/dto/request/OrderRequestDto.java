package com.seowon.coding.controller.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {
    @NotEmpty
    String customerName;
    @NotEmpty
    String customerEmail;
    @NotNull
    List<ProductDto> products;
}
