package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.Auth.RefreshTokenResponseDto;
import com.scientia.mercatus.entity.RefreshToken;
import com.scientia.mercatus.entity.User;

public interface IRefreshTokenService {
    String createRefreshToken(User user);
    RefreshToken validateRefreshToken(String rawRefreshToken);
    void revokeRefreshToken(String rawRefreshToken);
    RefreshTokenResponseDto rotateRefreshToken(String rawRefreshToken);
}
