package com.seowon.coding.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_CATEGORY_NAME("C001","유효하지 않는 카테고리입니다."),
    NOT_FOUND_PRODUCT("P001", "상품을 찾을수 없습니다.")
    ;

    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
