package com.seowon.coding.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductReqDto {
    private String customerName;
    private String customerEmail;
    private List<Long> productIds;
    private List<Integer> quantities;
}
