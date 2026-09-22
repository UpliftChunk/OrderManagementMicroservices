package com.ecommerce.Service.dto;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long productId;
    private String name;
    private Double price;
    private String description;
}