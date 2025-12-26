package com.seowon.coding.domain.repository;

import com.seowon.coding.domain.model.OrderItem;
import com.seowon.coding.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByProduct(Product product);
}
