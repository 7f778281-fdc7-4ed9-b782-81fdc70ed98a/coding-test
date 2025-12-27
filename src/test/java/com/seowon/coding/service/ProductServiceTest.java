package com.seowon.coding.service;

import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(1L)
                .name("Test Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100.00))
                .stockQuantity(10)
                .category("Electronics")
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
                .description("Description 2")
                .price(BigDecimal.valueOf(200.00))
                .stockQuantity(20)
                .category("Books")
                .build();
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Optional<Product> product = productService.getProductById(1L);

        assertTrue(product.isPresent());
        assertEquals("Test Product 1", product.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void createProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product created = productService.createProduct(product1);

        assertNotNull(created);
        assertEquals("Test Product 1", created.getName());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void updateProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product updated = productService.updateProduct(1L, product1);

        assertNotNull(updated);
        assertEquals("Test Product 1", updated.getName());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void deleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void findProductsByCategory() {
        when(productRepository.findByCategory("Electronics")).thenReturn(List.of(product1));

        List<Product> products = productService.findProductsByCategory("Electronics");

        assertEquals(1, products.size());
        assertEquals("Electronics", products.get(0).getCategory());

        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    void applyBulkPriceChange_WithTax() {
        // Given
        List<Long> productIds = Arrays.asList(1L, 2L);
        BigDecimal percentage = new BigDecimal("10"); // 10% 인상
        
        when(productRepository.findAllById(productIds)).thenReturn(Arrays.asList(product1, product2));
        
        // When
        productService.applyBulkPriceChange(productIds, percentage, true);
        
        // Then
        // Product1: 100 * 1.1 * 1.1 (세금 10%) = 121.00
        assertEquals(0, new BigDecimal("121.00").compareTo(product1.getPrice()));
        
        // Product2: 200 * 1.1 * 1.1 (세금 10%) = 242.00
        assertEquals(0, new BigDecimal("242.00").compareTo(product2.getPrice()));
        
        verify(productRepository, times(1)).findAllById(productIds);
    }

    @Test
    void applyBulkPriceChange_NegativePercentage() {
        // Given - 10% 인하
        List<Long> productIds = List.of(1L);
        BigDecimal percentage = new BigDecimal("-10");
        
        when(productRepository.findAllById(productIds)).thenReturn(List.of(product1));
        
        // When
        productService.applyBulkPriceChange(productIds, percentage, false);
        
        // Then
        // Product1: 100 * 0.9 = 90.00
        assertEquals(0, new BigDecimal("90.00").compareTo(product1.getPrice()));
    }

    @Test
    void applyBulkPriceChange_EmptyProductIds() {
        // Given
        List<Long> emptyIds = List.of();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productService.applyBulkPriceChange(emptyIds, BigDecimal.TEN, false)
        );
    }

    @Test
    void applyBulkPriceChange_NullPercentage() {
        // Given
        List<Long> productIds = List.of(1L);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productService.applyBulkPriceChange(productIds, null, false)
        );
    }

}