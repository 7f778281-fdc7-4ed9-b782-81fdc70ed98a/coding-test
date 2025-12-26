package com.seowon.coding.domain.dto.order.req;

import java.util.List;

public record RequestDTO(
        String customerName,
        String customerEmail,
        List<OrderProduct> products
) {
}
