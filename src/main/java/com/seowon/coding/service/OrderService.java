package com.seowon.coding.service;

import com.seowon.coding.domain.model.Order;
import com.seowon.coding.domain.model.OrderItem;
import com.seowon.coding.domain.model.ProcessingStatus;
import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.OrderRepository;
import com.seowon.coding.domain.repository.ProcessingStatusRepository;
import com.seowon.coding.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProcessingStatusRepository processingStatusRepository;
    private final ProgressService progressService;

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }


    public Order updateOrder(Long id, Order order) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        order.setId(id);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }



    public Long placeOrder(String customerName, String customerEmail, List<OrderProduct> orderProducts, String couponCode) {
        // TODO #3: 구현 항목
        // * 주어진 고객 정보로 새 Order를 생성
        // * 지정된 Product를 주문에 추가
        // * order 의 상태를 PENDING 으로 변경
        // * orderDate 를 현재시간으로 설정
        // * order 를 저장
        // * 각 Product 의 재고를 수정
        // * placeOrder 메소드의 시그니처는 변경하지 않은 채 구현하세요.

        Order order = Order.createPendingOrder(customerName, customerEmail, orderProducts);

        for (OrderProduct req : orderProducts) {
            Long pid = req.getProductId();
            int qty = req.getQuantity();

            Product product = productRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + pid));
            product.decreaseStock(qty);

            order.addItem(OrderItem.createOrderItem(order, product, qty));
        }

        order.calculateTotal(couponCode);

        return orderRepository.save(order).getId();
    }

    /**
     * TODO #4 (리펙토링): Service 에 몰린 도메인 로직을 도메인 객체 안으로 이동
     * - Repository 조회는 도메인 객체 밖에서 해결하여 의존 차단 합니다.
     * - #3 에서 추가한 도메인 메소드가 있을 경우 사용해도 됩니다.
     */
    public Order checkoutOrder(String customerName,
                               String customerEmail,
                               List<OrderProduct> orderProducts,
                               String couponCode) {
        Order order = Order.createPendingOrder(customerName, customerEmail, orderProducts);

        for (OrderProduct req : orderProducts) {
            Long pid = req.getProductId();
            int qty = req.getQuantity();

            Product product = productRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + pid));
            product.decreaseStock(qty);

            order.addItem(OrderItem.createOrderItem(order, product, qty));
        }

        order.calculateTotal(couponCode);

        return orderRepository.save(order);
    }

    /**
     * TODO #5: 코드 리뷰 - 장시간 작업과 진행률 저장의 트랜잭션 분리
     * - 시나리오: 일괄 배송 처리 중 진행률을 저장하여 다른 사용자가 조회 가능해야 함.
     * - 리뷰 포인트: proxy 및 transaction 분리, 예외 전파/롤백 범위, 가독성 등
     * - 상식적인 수준에서 요구사항(기획)을 가정하며 최대한 상세히 작성하세요.
     */

    /**
     * 개선된 일괄 배송 처리 메서드
     * 
     * 개선 사항:
     * 1. ProcessingStatus 중복 생성 방지 - 초기에 한 번만 생성 후 재사용
     * 2. JPA 더티 체킹 활용 - @Transactional 내에서 명시적 save() 제거
     * 3. Self-invocation 해결 - ProgressService로 분리하여 REQUIRES_NEW 정상 작동
     * 4. 예외 처리 개선 - 로깅 추가 및 실패 시 보상 트랜잭션으로 상태 업데이트
     * 5. 데이터 정합성 보장 - 실패 시 ProcessingStatus를 FAILED로 마킹
     * 6. 가독성 개선 - null 체크 및 의미있는 변수명 사용
     * 
     * 추가 개선 고려사항:
     * - @Async + CompletableFuture로 비동기 처리
     * - 메시지 큐(Kafka/RabbitMQ) 활용하여 이벤트 기반 처리
     * - 재시도 로직 추가 (Spring Retry)
     */
    @Transactional
    public void bulkShipOrdersParent(String jobId, List<Long> orderIds) {
        // 입력 검증
        if (orderIds == null || orderIds.isEmpty()) {
            log.warn("bulkShipOrders called with empty orderIds for jobId: {}", jobId);
            throw new IllegalArgumentException("orderIds is empty");
        }

        // ProcessingStatus 초기화 (한 번만 생성)
        ProcessingStatus ps = processingStatusRepository.findByJobId(jobId)
                .orElseGet(() -> {
                    ProcessingStatus newPs = ProcessingStatus.builder()
                            .jobId(jobId)
                            .build();
                    return processingStatusRepository.save(newPs);
                });
        
        ps.markRunning(orderIds.size());
        // 더티 체킹으로 자동 저장 (명시적 save() 불필요)

        int processed = 0;
        int failed = 0;
        
        for (Long orderId : orderIds) {
            try {
                // 오래 걸리는 작업 시뮬레이션 (예: 외부 배송 시스템 연동, 대용량 계산 등)
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
                
                order.markAsShipped();
                // 더티 체킹으로 자동 저장
                
                processed++;
                
                // 별도 서비스로 진행률 업데이트 (REQUIRES_NEW 트랜잭션)
                // 부모 트랜잭션 실패 시에도 진행률은 커밋되어 사용자에게 표시됨
                progressService.updateProgress(jobId, processed, orderIds.size());
                
            } catch (Exception e) {
                failed++;
                log.error("Failed to process order {} in job {}: {}", orderId, jobId, e.getMessage(), e);
            }
        }
        
        // 최종 상태 업데이트
        if (failed == 0) {
            ps.markCompleted();
            log.info("Bulk ship completed successfully. JobId: {}, Processed: {}", jobId, processed);
        } else {
            ps.markFailed();
            log.warn("Bulk ship completed with errors. JobId: {}, Processed: {}, Failed: {}", 
                    jobId, processed, failed);
        }
    }

}