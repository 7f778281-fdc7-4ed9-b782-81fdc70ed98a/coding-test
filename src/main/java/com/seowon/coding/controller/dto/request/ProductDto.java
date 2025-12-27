package com.seowon.coding.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductDto {
    @NotNull
    Long productId;
    @NotNull
    @Min(0)
    Integer quantity;
}
