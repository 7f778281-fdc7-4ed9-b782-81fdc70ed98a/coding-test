package com.seowon.coding.domain.dto;

import com.seowon.coding.service.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    private String customerName;
    private String customerEmail;
    private List<OrderProduct> products;

    public List<Long> getProductIds() {
        return products.stream().map(OrderProduct::getProductId).toList();
    }

    public List<Integer> getQuantities() {
        return products.stream().map(OrderProduct::getQuantity).toList();
    }
}
