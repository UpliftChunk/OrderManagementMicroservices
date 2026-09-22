package com.ecommerce.security;

import com.ecommerce.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityAutoConfiguration {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secret);
    }

}