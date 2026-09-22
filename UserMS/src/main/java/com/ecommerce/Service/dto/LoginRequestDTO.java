package com.ecommerce.Service.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email
    private String email;
    private String password;
}