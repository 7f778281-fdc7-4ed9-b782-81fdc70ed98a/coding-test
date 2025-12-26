package com.seowon.coding.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {
    private String customerName;
    private String customerEmail;
    private List<Long> productIds;
    private List<Integer> quantities;
}
