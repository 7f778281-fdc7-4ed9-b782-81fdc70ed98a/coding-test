package com.seowon.coding.domain.repository;

import com.seowon.coding.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByStockQuantityGreaterThan(int minStock);

    // 카테고리별 제품 조회
    @Query(value = "SELECT p FROM Product p where p.category = :category")
    List<Product> findProductsByCategory(@Param("category") String category);
}