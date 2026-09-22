package com.ecommerce.Service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventoryRequestDTO {

    private Long productId;
    private Integer quantity;
}