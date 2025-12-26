package com.seowon.coding.controller.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderRequestDTO {
    private String customerName;
    @Email
    private String customerEmail;
    private List<OrderInfoVO> orderInfo;

    public List<Long> getProductIdList() {
        return orderInfo.stream()
                .map(OrderInfoVO::getProductId)
                .toList();
    }

    public List<Integer> getQuantityList() {
        return orderInfo.stream()
                .map(OrderInfoVO::getQuantity)
                .toList();
    }
}
