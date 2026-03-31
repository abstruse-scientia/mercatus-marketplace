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
        // Use the user's existing opaque identifier (set during registration)
        // This ensures the JWT contains the exact same identifier as stored in the database
        String opaqueId = userDetail.getOpaqueIdentifier();
        
        // If for some reason it's null, generate one (shouldn't happen if user was properly registered)
        if (opaqueId == null || opaqueId.isEmpty()) {
            opaqueId = userIdentifierService.getOrCreateOpaqueIdentifier(userDetail);
        }

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
