package com.ecommerce.Service.exceptionHandler.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InsufficientStockException extends RuntimeException {

    private final List<Long> failedProductIds;

    public InsufficientStockException(
            String message,
            List<Long> failedProductIds
    ) {
        super(message);
        this.failedProductIds = failedProductIds;
    }
}