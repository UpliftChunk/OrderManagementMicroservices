package com.ecommerce.Service.dto;

import com.ecommerce.Service.entity.RoleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    @Email
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType role;
}