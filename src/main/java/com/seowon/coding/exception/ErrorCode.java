package com.seowon.coding.exception;

public enum ErrorCode {
    INVALID_CATEGORY_NAME("C001","유효하지 않는 카테고리입니다.");

    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
