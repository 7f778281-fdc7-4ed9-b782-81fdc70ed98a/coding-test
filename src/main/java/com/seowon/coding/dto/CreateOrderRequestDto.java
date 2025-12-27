package com.seowon.coding.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOrderRequestDto {

    private String name;

    private String email;

    private List<OrderItemDto> orderItemDtos;
    
}
