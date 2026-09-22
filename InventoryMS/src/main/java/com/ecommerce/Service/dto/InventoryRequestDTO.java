package com.ecommerce.Service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventoryRequestDTO {

    @NotNull(message = "Product ID is required")
    @PositiveOrZero(message = "Product ID cannot be negative")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}