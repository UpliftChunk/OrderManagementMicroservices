package com.ecommerce.Service.service;

import com.ecommerce.Service.dto.LoginResponseDTO;
import com.ecommerce.Service.dto.UserRequestDTO;
import com.ecommerce.Service.dto.UserResponseDTO;
import com.ecommerce.Service.entity.User;
import com.ecommerce.Service.exceptionHandler.exception.InvalidCredentialsException;
import com.ecommerce.Service.repository.UserRepository;
//import com.ecommerce.Service.util.JwtUtil;
import com.ecommerce.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // REGISTER USER
    public UserResponseDTO register(UserRequestDTO dto) {

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        // 🔐 HASH PASSWORD
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);

        return mapToUserResponseDTOLocal(saved);
    }

    // LOGIN USER
    public LoginResponseDTO login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // 🔐 GENERATE JWT
        return mapToLoginResponseDTO(jwtUtil.generateToken(mapToUserResponseDTO(user)));
    }
    private UserResponseDTO mapToUserResponseDTOLocal(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
    private com.ecommerce.security.dto.UserResponseDTO mapToUserResponseDTO(User user) {
        com.ecommerce.security.dto.UserResponseDTO dto = new com.ecommerce.security.dto.UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        return dto;
    }

    private LoginResponseDTO mapToLoginResponseDTO(String token) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setToken(token);
        return dto;
    }
}