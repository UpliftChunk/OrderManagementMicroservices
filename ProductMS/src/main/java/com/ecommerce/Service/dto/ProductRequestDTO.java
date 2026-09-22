package com.ecommerce.Service.dto;

import lombok.Data;

@Data
public class ProductRequestDTO {
    private String name;
    private Double price;
    private String description;
    private Integer quantity;
}