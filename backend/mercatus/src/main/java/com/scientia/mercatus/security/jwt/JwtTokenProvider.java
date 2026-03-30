package com.scientia.mercatus.security.jwt;

import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.security.UserIdentifierService;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtKeyProvider jwtKeyProvider;
    private final UserIdentifierService userIdentifierService;

    public String generateJwtToken(User userDetail){
        String opaqueId = userIdentifierService.getOrCreateOpaqueIdentifier(userDetail);

        return Jwts.builder()
                .issuer("Mercatus")
                .subject("user_token")
                .claim("uid", opaqueId)
                .claim("roles", userDetail.getRoles().stream().map(role -> role.getName().toString())
                        .collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + 30 * 60 * 1000))
                .signWith(jwtKeyProvider.getSecretKey())
                .compact();
    }
}
