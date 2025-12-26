package com.seowon.coding.dto.request;

import com.seowon.coding.domain.model.OrderItem;
import com.seowon.coding.domain.model.Product;
import java.util.List;
import lombok.Data;

@Data
public class OrderRequestDto {
    /**
     * TODO #2: 주문을 생성하는 API 구현
     * 구현목록:
     * 1. Request DTO 를 받아서 주문 생성
     * 2. orderService.placeOrder 호출
     * 3. 주문 생성시 HTTP 201 CREATED 반환
     * 4. 필요한 DTO 생성
     *
     * Request body 예시:
     * {
     *   "customerName": "John Doe",
     *   "customerEmail": "john@example.com",
     *   "products": [
     *     {"productId": 1, "quantity": 2},
     *     {"productId": 3, "quantity": 1}
     *   ]
     * }
     */

    private String customerName;
    private String customerEmail;
    private List<Long> productIds;
    private List<Integer> quantities;
}
