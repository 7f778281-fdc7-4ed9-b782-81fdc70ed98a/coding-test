package com.seowon.coding.domain.dto;

import java.util.List;

public record CreateOrderDTO(
        String customerName,
        String customerEmail,
        List<ProductDTO> products
) {

}
