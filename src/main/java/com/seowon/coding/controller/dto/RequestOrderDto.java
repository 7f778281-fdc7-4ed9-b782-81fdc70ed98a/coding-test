package com.seowon.coding.controller.dto;

import java.util.List;

import lombok.Data;

public class RequestOrderDto {

	@Data
	public static class PlaceOrder {
		private String customerName;
		private String customerEmail;
		private List<Product> products;

	}
	@Data
	public static class Product {
		private Long productId;
		private Integer quantity;
	}

}
