package com.seowon.coding.domain.enumuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum TaxVat {

    DEFAULT(10);

    private int vat;
}
