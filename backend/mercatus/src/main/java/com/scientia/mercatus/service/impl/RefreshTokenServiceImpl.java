package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.RefreshToken;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.repository.RefreshTokenRepository;
import com.scientia.mercatus.service.IRefreshTokenService;
import com.scientia.mercatus.util.IRefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {


    private final RefreshTokenRepository refreshTokenRepository;
    private final IRefreshTokenUtil refreshTokenUtil;


    @Override
    public String createRefreshToken(User user){
        var refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        String rawToken = refreshTokenUtil.generateRawToken();
        String hashedToken = refreshTokenUtil.hashToken(rawToken);
        refreshToken.setTokenHash(hashedToken);
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Override
    public boolean validateRefreshToken(String rawRefreshToken){
        String hashedToken = refreshTokenUtil.hashToken(rawRefreshToken);
        var refreshToken = refreshTokenRepository.findByTokenHash(hashedToken);
        Instant currentTime = Instant.now();
        if (refreshToken.isEmpty()) {
            return false;
        }
        else if (refreshToken.get().isRevoked()) {
            return false;
        }
        else if (refreshToken.get().getExpiryDate().isBefore(currentTime)) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void revokeRefreshToken(String rawRefreshToken) {
        String hashedToken = refreshTokenUtil.hashToken(rawRefreshToken);
        var refreshToken= refreshTokenRepository.findByTokenHash(hashedToken);
        if (refreshToken.isEmpty()) {
            return;
        }
        refreshToken.get().setRevoked(true);

    }

    @Override
    public String rotateRefreshToken(String rawRefreshToken) {
        if (!validateRefreshToken(rawRefreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        revokeRefreshToken(rawRefreshToken);
        String hashedToken = refreshTokenUtil.hashToken(rawRefreshToken);
        var existingToken = refreshTokenRepository.findByTokenHash(hashedToken);
        User user = existingToken.get().getUser();
        return createRefreshToken(user);

    }

}
