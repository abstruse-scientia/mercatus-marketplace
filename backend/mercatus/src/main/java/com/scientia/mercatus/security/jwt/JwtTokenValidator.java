package com.scientia.mercatus.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtKeyProvider jwtKeyProvider;

    public Claims validateAndParseClaims(String token) throws JwtException {
        Claims claims = io.jsonwebtoken.Jwts.parser()
            .verifyWith(jwtKeyProvider.getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        if (claims.containsKey("uid")) {
            log.debug("New format JWT with opaque identifier detected");
            return claims;
        } else if (claims.containsKey("email")) {
            log.warn("Old format JWT detected - please re-authenticate for updated token format");
            return claims;
        }

        throw new JwtException("Invalid token format: missing required claims");
    }
}

