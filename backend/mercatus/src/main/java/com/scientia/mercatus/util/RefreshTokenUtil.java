package com.scientia.mercatus.util;

public interface RefreshTokenUtil {
    String hashToken(String token);
    String generateRawToken();
}
