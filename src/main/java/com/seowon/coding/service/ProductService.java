package com.seowon.coding.service;

import com.seowon.coding.domain.enumuration.TaxVat;
import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
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
     * TODO #6 (리펙토링): 대량 가격 변경 로직을 도메인 객체 안으로 리팩토링하세요.
     */
    public void applyBulkPriceChange(List<Long> productIds, double percentage, boolean includeTax) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("empty productIds");
        }
        // Entity를 리스트에 담아 한번에 저장하도록 하기 위함
        List<Product> productList = new ArrayList<>();

        for(Long id : productIds) {
            Product p = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
            // Entity 내부 Method에서 처리하도록 개선
            p.priceChange(percentage, includeTax, TaxVat.DEFAULT);

            // 매 회차마다 저장하지 않고 한번에 저장하도록 하기 위해 리스트에 add
            productList.add(p);
        }
        // saveAll(list)로 여러번 반복 저장이 아닌 한번에 모든 Entity 리스트 저장
        productRepository.saveAll(productList);

    }
}
