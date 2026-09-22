package com.ecommerce.Service.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequestDTO {
    private Long userId;

    @Valid
    private List<OrderItemDTO> items;
}
