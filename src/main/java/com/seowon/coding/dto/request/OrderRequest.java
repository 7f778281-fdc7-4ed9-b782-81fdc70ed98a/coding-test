package com.seowon.coding.dto.request;

import com.seowon.coding.dto.ProductsDto;

import java.util.List;

public record OrderRequest(
        String customerName,
        String customerEmail,
        List<ProductsDto> products
) {}