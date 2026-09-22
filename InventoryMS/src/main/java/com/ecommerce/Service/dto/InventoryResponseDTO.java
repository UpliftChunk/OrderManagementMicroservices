package com.ecommerce.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryResponseDTO {
    private Boolean success;
    private String message;
}