package com.seowon.coding.dto;

import java.util.List;

import com.seowon.coding.domain.model.Order;
import com.seowon.coding.domain.model.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponseDto {

    private String customerName;

    private String customerEmail;

    private List<OrderItemDto> orderItems;

    public static CreateOrderResponseDto from(Order order) {
        List<OrderItemDto> orderItemDtos = null;
        if (!order.getItems().isEmpty()) {

            for (OrderItem item : order.getItems()) {
                orderItemDtos.add(new OrderItemDto(item.getId(), item.getQuantity()));
            }
            
        }

        return CreateOrderResponseDto.builder().customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail()).orderItems(orderItemDtos).build();
    }

}
