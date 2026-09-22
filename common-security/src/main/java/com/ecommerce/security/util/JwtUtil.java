package com.ecommerce.security.util;

import com.ecommerce.security.dto.UserResponseDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtUtil {

    private final Key key;

    public JwtUtil(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate token
    public String generateToken(UserResponseDTO user) {

        Map<String,Object> claims =
                new HashMap<>();

        claims.put("role", user.getRole());
//        claims.put("name", user.getName());    // may get updated - update anomaly
//        claims.put("email", user.getEmail());  // may get updated - update anomaly

        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {

        return Long.parseLong(
                getClaims(token).getSubject()
        );

    }

    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);

    }

    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (ExpiredJwtException | MalformedJwtException e) {
            return false;
        }
        catch (JwtException e) {
            return false;
        }
    }
}