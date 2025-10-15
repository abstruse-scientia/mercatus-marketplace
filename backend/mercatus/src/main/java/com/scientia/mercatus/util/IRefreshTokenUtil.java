package com.scientia.mercatus.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


@Component
public class IRefreshTokenUtil implements RefreshTokenUtil{
    @Override
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : encodedHash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }

    }

    @Override
    public String generateRawToken() {
         SecureRandom secureRandom = new SecureRandom();
         Base64.Encoder encode = Base64.getUrlEncoder().withoutPadding();
         byte[] randomBytes = new byte[64];
         secureRandom.nextBytes(randomBytes);
         return encode.encodeToString(randomBytes);
    }
}
