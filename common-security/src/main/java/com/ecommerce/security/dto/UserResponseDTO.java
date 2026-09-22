package com.ecommerce.security.dto;

import com.ecommerce.security.entity.RoleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String name;
    private String email;

    private String role;

    public void setRole(String role) {

        try {
            RoleType.valueOf(role.toUpperCase());
            this.role = role.toUpperCase();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

    }
}