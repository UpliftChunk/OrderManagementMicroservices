package com.ecommerce.Service.dto;

import com.ecommerce.Service.entity.RoleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private RoleType role;
}