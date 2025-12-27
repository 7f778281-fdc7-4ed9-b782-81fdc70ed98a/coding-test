package com.seowon.coding.controller.dto;

import com.seowon.coding.service.OrderProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    String customerName;
    String customerEmail;
    List<OrderProduct> products;
}
