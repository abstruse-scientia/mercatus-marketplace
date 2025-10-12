package com.scientia.mercatus.security.jwt;


import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtKeyProvider {

    private final Environment env;

    @Getter
    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        String secret = env.getProperty("SECRET_KEY");
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("Secret key has not been set");
        }
        if (secret.length() != 32) {
            throw new IllegalStateException("Secret key must be 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

}
