package com.seowon.coding.service;

import com.seowon.coding.domain.model.Order;
import com.seowon.coding.domain.model.OrderItem;
import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.OrderRepository;
import com.seowon.coding.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order1;
    private Order order2;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(1L)
                .name("Test Product 1")
                .price(BigDecimal.valueOf(100.00))
                .stockQuantity(10)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
                .price(BigDecimal.valueOf(200.00))
                .stockQuantity(20)
                .build();

        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .product(product1)
                .quantity(2)
                .price(product1.getPrice())
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(2L)
                .product(product2)
                .quantity(1)
                .price(product2.getPrice())
                .build();

        order1 = Order.builder()
                .id(1L)
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(400.00))
                .build();
        order1.addItem(item1);
        order1.addItem(item2);

        order2 = Order.builder()
                .id(2L)
                .customerName("Jane Smith")
                .customerEmail("jane@example.com")
                .status(Order.OrderStatus.PROCESSING)
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(200.00))
                .build();
        order2.addItem(item2);
    }

    @Test
    void getAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        Optional<Order> order = orderService.getOrderById(1L);

        assertTrue(order.isPresent());
        assertEquals("John Doe", order.get().getCustomerName());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void updateOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        Order updated = orderService.updateOrder(1L, order1);

        assertNotNull(updated);
        assertEquals("John Doe", updated.getCustomerName());
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).save(order1);
    }

    @Test
    void deleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void placeOrder() {
        // Given - 테스트 데이터 준비
        String testCustomerName = "John Doe";
        String testCustomerEmail = "john@example.com";
        String testCouponCode = "";

        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(new OrderProduct(1L, 2));  // Product1 2개
        orderProducts.add(new OrderProduct(2L, 1));  // Product2 1개

        // Product 조회 mock 설정
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        
        // Order 저장 시 ID가 설정된 Order를 반환하도록 mock 설정
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);  // ID 설정
            return savedOrder;
        });

        // When - placeOrder 실행
        Long savedOrderId = orderService.placeOrder(
                testCustomerName, 
                testCustomerEmail, 
                orderProducts, 
                testCouponCode
        );

        // Then - 결과 검증
        assertNotNull(savedOrderId);
        assertEquals(1L, savedOrderId);

        // ArgumentCaptor로 저장된 Order 객체 캡처
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        
        Order capturedOrder = orderCaptor.getValue();
        
        // Order 기본 정보 검증
        assertEquals(testCustomerName, capturedOrder.getCustomerName());
        assertEquals(testCustomerEmail, capturedOrder.getCustomerEmail());
        assertEquals(Order.OrderStatus.PENDING, capturedOrder.getStatus());
        assertNotNull(capturedOrder.getOrderDate());
        
        // OrderItem 검증
        assertEquals(2, capturedOrder.getItems().size());
        
        // 첫 번째 아이템 검증
        OrderItem firstItem = capturedOrder.getItems().get(0);
        assertEquals(product1.getId(), firstItem.getProduct().getId());
        assertEquals(2, firstItem.getQuantity());
        assertEquals(product1.getPrice(), firstItem.getPrice());
        
        // 두 번째 아이템 검증
        OrderItem secondItem = capturedOrder.getItems().get(1);
        assertEquals(product2.getId(), secondItem.getProduct().getId());
        assertEquals(1, secondItem.getQuantity());
        assertEquals(product2.getPrice(), secondItem.getPrice());
        
        // 총 금액 검증 (Product1: 100 * 2 = 200, Product2: 200 * 1 = 200, 합계: 400, 배송비: 0)
        assertEquals(0, new BigDecimal("400.00").compareTo(capturedOrder.getTotalAmount()));
        
        // 재고 감소 검증
        assertEquals(8, product1.getStockQuantity());  // 10 - 2 = 8
        assertEquals(19, product2.getStockQuantity()); // 20 - 1 = 19
        
        // Repository 호출 검증
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

}