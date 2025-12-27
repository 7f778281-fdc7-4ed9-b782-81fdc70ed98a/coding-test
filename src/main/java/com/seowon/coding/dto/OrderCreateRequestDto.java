package com.seowon.coding.dto;

import com.seowon.coding.service.OrderProduct;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class OrderCreateRequestDto {
    private String customerName;
    private String customerEmail;
    private List<OrderProduct> products;

    public List<Long> getProductIds() {
        List<Long> ids = new ArrayList<>();
        for (OrderProduct orderProducts : products) {
            ids.add(orderProducts.getProductId());
        }
        return ids;
    }

    public List<Integer> getProductQuantities() {
        List<Integer> quantities = new ArrayList<>();
        for (OrderProduct orderProducts : products) {
            quantities.add(orderProducts.getQuantity());
        }
        return quantities;
    }
}
