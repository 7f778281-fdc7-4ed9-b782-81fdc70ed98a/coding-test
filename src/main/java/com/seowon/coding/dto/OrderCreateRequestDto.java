package com.seowon.coding.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class OrderCreateRequestDto {
    private String customerName;
    private String customerEmail;
    private List<OrderProducts> products;

    public List<Long> getProductIds() {
        List<Long> ids = new ArrayList<>();
        for (OrderProducts orderProducts : products) {
            ids.add(orderProducts.getProductId());
        }
        return ids;
    }

    public List<Integer> getProductQuantities() {
        List<Integer> quantities = new ArrayList<>();
        for (OrderProducts orderProducts : products) {
            quantities.add(orderProducts.getQuantity());
        }
        return quantities;
    }
}
