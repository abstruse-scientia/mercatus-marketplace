package com.scientia.mercatus.util;

public interface SecureTokenUtil {
    String hashToken(String token);
    String generateRawToken();
}
