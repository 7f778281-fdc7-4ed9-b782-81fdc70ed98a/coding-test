package com.seowon.coding.controller;

import com.seowon.coding.controller.dto.order.request.CreateOrderRequest;
import com.seowon.coding.controller.dto.order.request.CreateOrderRequest.ProductRequest;
import com.seowon.coding.domain.model.Order;
import com.seowon.coding.service.OrderService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        try {
            Order updatedOrder = orderService.updateOrder(id, order);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Order> createOrder(
        @RequestBody CreateOrderRequest createOrderRequest
    ) {
        List<ProductRequest> productRequests = createOrderRequest.products();

        List<Long> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (ProductRequest productRequest : productRequests) {
            productIds.add(productRequest.productId());
            quantities.add(productRequest.quantity());
        }

        Order order = orderService.placeOrder(
            createOrderRequest.customerName(),
            createOrderRequest.customerEmail(),
            productIds,
            quantities
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

}