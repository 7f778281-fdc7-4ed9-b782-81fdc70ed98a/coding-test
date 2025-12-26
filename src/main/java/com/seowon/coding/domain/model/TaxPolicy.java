package com.seowon.coding.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaxPolicy {
    CLOTHES("의류", 2.2),
    FOOD("식품", 5.0);

    private final String category;
    private final Double rate;

}
