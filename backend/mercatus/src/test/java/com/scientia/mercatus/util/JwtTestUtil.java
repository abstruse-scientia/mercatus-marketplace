package com.scientia.mercatus.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTestUtil {
    
    @Value("${secret.key}")
    private String secretKey;
    
    public String generateUserToken(String email) {
        String opaqueId = UUID.randomUUID().toString();
        return Jwts.builder()
            .issuer("Mercatus")
            .subject("user_token")
            .claim("uid", opaqueId)
            .claim("roles", "ROLE_USER")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }
    
    public String generateAdminToken(String email) {
        String opaqueId = UUID.randomUUID().toString();
        return Jwts.builder()
            .issuer("Mercatus")
            .subject("user_token")
            .claim("uid", opaqueId)
            .claim("roles", "ROLE_ADMIN")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }
    
    public String generateExpiredToken(String email) {
        String opaqueId = UUID.randomUUID().toString();
        return Jwts.builder()
            .issuer("Mercatus")
            .subject("user_token")
            .claim("uid", opaqueId)
            .claim("roles", "ROLE_USER")
            .issuedAt(new Date(System.currentTimeMillis() - 3600000))
            .expiration(new Date(System.currentTimeMillis() - 1800000))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }
    
    public String generateTokenWithRole(String email, String role) {
        String opaqueId = UUID.randomUUID().toString();
        return Jwts.builder()
            .issuer("Mercatus")
            .subject("user_token")
            .claim("uid", opaqueId)
            .claim("roles", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }
}

