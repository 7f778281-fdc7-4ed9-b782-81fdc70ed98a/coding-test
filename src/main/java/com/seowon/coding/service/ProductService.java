package com.seowon.coding.service;

import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.ProductRepository;
import com.seowon.coding.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        validateExsistById(id);
        product.setId(id);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        validateExsistById(id);
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> findProductsByCategory(String category) {
        // TODO #1: 구현 항목
        // Repository를 사용하여 category 로 찾을 제품목록 제공
        invalidateCategoryName(category);
        return productRepository
                .findByCategory(category.trim());
    }

    /**
     * TODO #6 (리펙토링): 대량 가격 변경 로직을 도메인 객체 안으로 리팩토링하세요.
     */

    public void applyBulkPriceChange(List<Long> productIds, double percentage, boolean includeTax) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.NOT_FOUND_PRODUCT.getMessage());
        }

        List<Product> products = productRepository.findAllById(productIds);


        if (products.isEmpty()) {
            throw new IllegalArgumentException("No products found for given IDs");
        }


        for (Product product : products) {
            product.updatePrice(percentage, includeTax);
        }
    }


    private static void invalidateCategoryName(final String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.INVALID_CATEGORY_NAME.getMessage());
        }
    }

    private void validateExsistById(final Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
    }
}
