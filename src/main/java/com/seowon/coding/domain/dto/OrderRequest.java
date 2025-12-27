package com.seowon.coding.domain.dto;

import java.util.List;

public record OrderRequest(
        String customerName,
        String customerEmail,
        List<Long> productIds,
        List<Integer> quantities
) {
}
