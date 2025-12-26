package com.seowon.coding.service;

import com.seowon.coding.domain.model.Order;
import com.seowon.coding.domain.model.OrderItem;
import com.seowon.coding.domain.model.ProcessingStatus;
import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.OrderRepository;
import com.seowon.coding.domain.repository.ProcessingStatusRepository;
import com.seowon.coding.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProcessingStatusRepository processingStatusRepository;
    
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


    @Transactional
    public Order placeOrder(String customerName, String customerEmail, List<Long> productIds, List<Integer> quantities) {
        // TODO #3: 구현 항목
        // * 주어진 고객 정보로 새 Order를 생성
        // * 지정된 Product를 주문에 추가
        // * order 의 상태를 PENDING 으로 변경
        // * orderDate 를 현재시간으로 설정
        // * order 를 저장
        // * 각 Product 의 재고를 수정
        // * placeOrder 메소드의 시그니처는 변경하지 않은 채 구현하세요.

        Order order = Order.builder()
                .customerName(customerName)
                .customerEmail(customerEmail)
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now()) // 또는 @CreationTimestamp 를 orderDate 필드에 정의
                .build();

        List<Product> productList = productRepository.findAllById(productIds);

        for(int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            int quantity = quantities.get(i);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();

            order.addItem(item);

            // order 생성 이후 다시 반복하지 않고 한번에 decrease 처리
            product.decreaseStock(quantity);
        }

        // CascadeType.ALL로 인해 OrderItem 동시저장
        // order만 저장하도록 하는 것으로 OrderItem Save 생략
        orderRepository.save(order);
        productRepository.saveAll(productList);

        return order;
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
        if (customerName == null || customerEmail == null) {
            throw new IllegalArgumentException("customer info required");
        }
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new IllegalArgumentException("orderReqs invalid");
        }

        Order order = Order.builder()
                .customerName(customerName)
                .customerEmail(customerEmail)
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();


        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderProduct req : orderProducts) {
            Long pid = req.getProductId();
            int qty = req.getQuantity();

            Product product = productRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + pid));
            if (qty <= 0) {
                throw new IllegalArgumentException("quantity must be positive: " + qty);
            }
            if (product.getStockQuantity() < qty) {
                throw new IllegalStateException("insufficient stock for product " + pid);
            }

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .price(product.getPrice())
                    .build();
            order.getItems().add(item);

            product.decreaseStock(qty);
            subtotal = subtotal.add(product.priceMultiply(qty)); // multiply 로직을 priceMultiply Method로 개선
        }

        order.checkoutTotalAmount(subtotal, couponCode); // shipping, discount 연산을 checkoutTotalAmount Method로 개선
        order.setStatus(Order.OrderStatus.PROCESSING);
        return orderRepository.save(order);
    }

    /**
     * TODO #5: 코드 리뷰 - 장시간 작업과 진행률 저장의 트랜잭션 분리
     * - 시나리오: 일괄 배송 처리 중 진행률을 저장하여 다른 사용자가 조회 가능해야 함.
     * - 리뷰 포인트: proxy 및 transaction 분리, 예외 전파/롤백 범위, 가독성 등
     * - 상식적인 수준에서 요구사항(기획)을 가정하며 최대한 상세히 작성하세요.
     */
    @Transactional
    public void bulkShipOrdersParent(String jobId, List<Long> orderIds) {
        ProcessingStatus ps = processingStatusRepository.findByJobId(jobId)
                .orElseGet(() -> processingStatusRepository.save(ProcessingStatus.builder().jobId(jobId).build()));
        ps.markRunning(orderIds == null ? 0 : orderIds.size());
        processingStatusRepository.save(ps);

        int processed = 0;
        for (Long orderId : (orderIds == null ? List.<Long>of() : orderIds)) {
            /**
             * catch에서 별도의 처리가 없다는것은 로그와 같은 기록이 전혀 남지 않고 아무 문제가 없었다는 듯이 넘어가는 처리가 되므로
             * 명확한 이유가 있는게 아니라면 지양해야 한다고 생각합니다.
             * 최소한 로그는 남겨야 하며 지금과 같이 @Transactional 로 트랜잭션 관리를 하고 있는 상황에서는 예외를 던져 롤백을 처리해야 한다고 생각합니다.
             */
            try {
                // 오래 걸리는 작업 이라는 가정 시뮬레이션 (예: 외부 시스템 연동, 대용량 계산 등)
                orderRepository.findById(orderId).ifPresent(o -> o.setStatus(Order.OrderStatus.PROCESSING));
                // 중간 진행률 저장
                this.updateProgressRequiresNew(jobId, ++processed, orderIds.size());
            } catch (Exception e) {
            }
        }
        /**
         * 이미 메서드 시작 부분에서 ProcessingStatus를 조회해 영속화된 상태인데 초기화 되지 않았음에도 동일한 데이터의 추가 조회는 불필요하다고 생각합니다.
         * 재 조회를 하지 않더라도 기존 ps 객체를 통해 Complete로 전환하는게 가능하기 때문입니다.
         */
        ps = processingStatusRepository.findByJobId(jobId).orElse(ps);
        ps.markCompleted();
        processingStatusRepository.save(ps);
    }

    /**
        REQUIRES_NEW 로 정의하게 됨으로써
        접근시마다 새로운 트랜잭션을 갖게 되어 기존 트랜잭션과는 독립된 트랜잭션에서 동작하게 됩니다.
        중간 진행률을 저장하기 위한 목적이라는 점에서 REQUIRES_NEW 가 적합한 선택이지만
        상위 메서드인 bulkShipOrdersParent 메서드에서 예외 발생시 같이 롤백이 되지 않기 때문에
        데이터 정합성 측면에서의 이슈를 잘 고려해야 한다고 생각합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProgressRequiresNew(String jobId, int processed, int total) {
        ProcessingStatus ps = processingStatusRepository.findByJobId(jobId)
                .orElseGet(() -> ProcessingStatus.builder().jobId(jobId).build());
        ps.updateProgress(processed, total);
        processingStatusRepository.save(ps);
    }

}