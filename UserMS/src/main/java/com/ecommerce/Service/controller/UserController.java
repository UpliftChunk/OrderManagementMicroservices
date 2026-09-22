package com.ecommerce.Service.controller;

import com.ecommerce.Service.dto.LoginRequestDTO;
import com.ecommerce.Service.dto.LoginResponseDTO;
import com.ecommerce.Service.dto.UserRequestDTO;
import com.ecommerce.Service.dto.UserResponseDTO;
import com.ecommerce.Service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // REGISTER
    @PostMapping("/register")
    public UserResponseDTO register(@Valid  @RequestBody UserRequestDTO dto) {
        return userService.register(dto);
    }

    // LOGIN
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return userService.login(dto.getEmail(), dto.getPassword());
    }
}