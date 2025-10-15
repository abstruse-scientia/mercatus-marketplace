package com.scientia.mercatus.service;

import com.scientia.mercatus.entity.RefreshToken;
import com.scientia.mercatus.entity.User;

public interface IRefreshTokenService {
    String createRefreshToken(User user);
    boolean validateRefreshToken(String rawRefreshToken);
    void revokeRefreshToken(String rawRefreshToken);
    String rotateRefreshToken(String rawRefreshToken);
}
