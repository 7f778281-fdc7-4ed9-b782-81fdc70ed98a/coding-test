package com.seowon.coding.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class OrderRequestDto {
    private String customerName;

    private String customerEmail;

    private List<ProductDto> products;
}


