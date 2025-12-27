package com.seowon.coding.service;

import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.model.TaxPolicy;
import com.seowon.coding.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        product.setId(id);
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> findProductsByCategory(String category) {
        // TODO #1: 구현 항목
        // Repository를 사용하여 category 로 찾을 제품목록 제공
        return productRepository.findByCategory(category);
    }

    /**
     * TODO #6 (리팩토링): 대량 가격 변경 로직을 도메인 객체 안으로 리팩토링
     * 
     * 개선 사항:
     * 1. BigDecimal 사용 - double의 부동소수점 오류 방지
     * 2. 일괄 조회 (findAllById) - 개별 조회 문제 해결
     * 3. 세금 정책 분리 - TaxPolicy 전략 패턴으로 확장 가능
     * 4. 도메인 로직 이동 - Product.applyPriceChange()로 캡슐화
     * 5. 더티 체킹 활용 - 명시적 save() 불필요
     * 6. 에러 핸들링 개선 - 개별 실패 시에도 다른 상품은 계속 처리
     */
    public void applyBulkPriceChange(List<Long> productIds, BigDecimal percentage, boolean includeTax) {
        // 입력 검증
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("productIds cannot be empty");
        }
        
        if (percentage == null) {
            throw new IllegalArgumentException("percentage cannot be null");
        }
        
        // 일괄 조회로 개별 조회 해결
        List<Product> products = productRepository.findAllById(productIds);
        
        if (products.isEmpty()) {
            log.warn("No products found for the given IDs");
            throw new IllegalArgumentException("No products found for the given IDs");
        }
        
        // 세금 정책 결정
        TaxPolicy taxPolicy = includeTax ? TaxPolicy.standard() : null;
        
        // 각 상품에 대해 도메인 로직 실행
        int successCount = 0;
        int failCount = 0;
        
        for (Product product : products) {
            try {
                product.applyPriceChange(percentage, taxPolicy);
                successCount++;
                log.debug("Price changed for product {}: {}", product.getId(), product.getPrice());
            } catch (Exception e) {
                failCount++;
                log.error("Failed to change price for product {}: {}", product.getId(), e.getMessage());
                // 개별 실패 시에도 계속 처리
            }
        }
        // 더티 체킹으로 자동 저장
    }
}