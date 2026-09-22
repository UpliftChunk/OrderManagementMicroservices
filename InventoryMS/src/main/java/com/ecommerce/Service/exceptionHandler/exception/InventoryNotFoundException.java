package com.ecommerce.Service.exceptionHandler.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InventoryNotFoundException extends RuntimeException {

    private final List<Long> failedProductIds;

    public InventoryNotFoundException(
            String message,
            List<Long> failedProductIds
    ) {
        super(message);
        this.failedProductIds = failedProductIds;
    }

    public InventoryNotFoundException(String message) {
        super(message);
        this.failedProductIds = null;
    }
}
