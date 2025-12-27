package com.seowon.coding.dto;

import com.seowon.coding.domain.model.Product;

import java.util.List;

public class CreateOrderDto {
    private String customerName;
    private String customerEmail;
    private List<Long> productIds;
    private List<Integer> quantities;

    private List<Product> products;
    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }
}
