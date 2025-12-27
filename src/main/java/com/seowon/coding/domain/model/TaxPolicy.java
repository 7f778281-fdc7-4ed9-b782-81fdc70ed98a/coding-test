package com.seowon.coding.domain.model;

import java.math.BigDecimal;

/**
 * 세금 정책 인터페이스
 * 카테고리별, 지역별로 다른 세금 정책을 적용할 수 있도록 전략 패턴 사용
 */
public interface TaxPolicy {
    
    /**
     * 세금이 포함된 가격을 계산
     * 
     * @param basePrice 기본 가격
     * @param product 상품 정보 (카테고리 등 참고용)
     * @return 세금이 포함된 가격
     */
    BigDecimal applyTax(BigDecimal basePrice, Product product);
    
    /**
     * 기본 VAT 정책 (10%)
     */
    static TaxPolicy standard() {
        return (basePrice, product) -> {
            BigDecimal taxRate = new BigDecimal("0.10");
            return basePrice.multiply(BigDecimal.ONE.add(taxRate));
        };
    }
    
    /**
     * 세금 없음
     */
    static TaxPolicy noTax() {
        return (basePrice, product) -> basePrice;
    }
    
    /**
     * 카테고리별 차등 세금 정책
     */
    static TaxPolicy categoryBased() {
        return (basePrice, product) -> {
            BigDecimal taxRate;
            if ("FOOD".equalsIgnoreCase(product.getCategory())) {
                taxRate = new BigDecimal("0.05"); // 식품 5%
            } else if ("LUXURY".equalsIgnoreCase(product.getCategory())) {
                taxRate = new BigDecimal("0.20"); // 사치품 20%
            } else {
                taxRate = new BigDecimal("0.10"); // 기본 10%
            }
            return basePrice.multiply(BigDecimal.ONE.add(taxRate));
        };
    }
}

