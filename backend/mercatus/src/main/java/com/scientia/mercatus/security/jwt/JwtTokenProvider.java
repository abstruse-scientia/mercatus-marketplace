package com.scientia.mercatus.security.jwt;

import com.scientia.mercatus.entity.User;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtKeyProvider jwtKeyProvider;

    public String generateJwtToken(Authentication authentication){
        String jwt = "";
        User fetchedUser = (User)authentication.getPrincipal();
        jwt = Jwts.builder().issuer("Mercatus").subject("JWT Token")
                .claim("username", fetchedUser.getName())
                .claim("email", fetchedUser.getEmail())
                .claim("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + 30 * 60 * 1000))
                .signWith(jwtKeyProvider.getSecretKey())
                .compact();
        return jwt;
    }
}
