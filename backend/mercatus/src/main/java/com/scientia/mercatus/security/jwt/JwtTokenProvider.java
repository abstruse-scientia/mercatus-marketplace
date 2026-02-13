package com.scientia.mercatus.security.jwt;

import com.scientia.mercatus.entity.Role;
import com.scientia.mercatus.entity.User;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class JwtTokenProvider {

    private final JwtKeyProvider jwtKeyProvider;

    public String generateJwtToken(User userDetail){
        String jwt = "";
        jwt = Jwts.builder().issuer("Mercatus").subject("JWT Token")
                .claim("username", userDetail.getUserName())
                .claim("email", userDetail.getEmail())
                .claim("roles", userDetail.getRoles().stream().map(role -> role.getName().toString())
                        .collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + 30 * 60 * 1000))
                .signWith(jwtKeyProvider.getSecretKey())
                .compact();
        return jwt;
    }
}
