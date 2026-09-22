package com.ecommerce.Service.exceptionHandler.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class NoReservedStockException extends RuntimeException {

    private final List<Long> failedProductIds;

    public NoReservedStockException(
            String message,
            List<Long> failedProductIds
    ) {
        super(message);
        this.failedProductIds = failedProductIds;
    }
}