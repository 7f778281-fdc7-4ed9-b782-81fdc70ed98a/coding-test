package com.seowon.coding.dto;

import java.util.List;

public record OrderRequest(
        String customerName,
        String email,
        List<ProductRequest> productRequests
) {
}
