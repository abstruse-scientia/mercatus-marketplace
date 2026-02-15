package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Auth.RefreshTokenResponseDto;
import com.scientia.mercatus.entity.RefreshToken;
import com.scientia.mercatus.entity.User;
import com.scientia.mercatus.exception.TokenExpiredException;
import com.scientia.mercatus.exception.TokenNotFoundException;
import com.scientia.mercatus.exception.TokenRevokedException;
import com.scientia.mercatus.repository.RefreshTokenRepository;
import com.scientia.mercatus.security.jwt.JwtTokenProvider;
import com.scientia.mercatus.service.IRefreshTokenService;
import com.scientia.mercatus.util.IRefreshTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;


@Profile("!test")
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {


    private final RefreshTokenRepository refreshTokenRepository;
    private final IRefreshTokenUtil refreshTokenUtil;
    private final JwtTokenProvider jwtTokenProvider;


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
    public RefreshToken validateRefreshToken(String rawRefreshToken){
        String hashedToken = refreshTokenUtil.hashToken(rawRefreshToken);
        var refreshToken = refreshTokenRepository.findByTokenHash(hashedToken);
        Instant currentTime = Instant.now();
        if (refreshToken.isEmpty()) {
            throw new TokenNotFoundException("Refresh token not found");
        }
        if (refreshToken.get().isRevoked()) {
            throw new TokenRevokedException("Token is already revoked");
        }
        if (refreshToken.get().getExpiryDate().isBefore(currentTime)) {
            throw new TokenExpiredException("Token has expired");
        }
        return refreshToken.get();
    }

    @Override
    public void revokeRefreshToken(String rawRefreshToken) {
        String hashedToken = refreshTokenUtil.hashToken(rawRefreshToken);
        var refreshToken= refreshTokenRepository.findByTokenHash(hashedToken);
        if (refreshToken.isEmpty()) {
            return;
        }
        refreshToken.get().setRevoked(true);
        refreshTokenRepository.save(refreshToken.get());

    }

    @Override
    @Transactional
    public RefreshTokenResponseDto rotateRefreshToken(String rawRefreshToken) {
        RefreshToken existingToken = validateRefreshToken(rawRefreshToken);
        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);
        User user = existingToken.getUser();
        String newRawRefreshToken = createRefreshToken(user);
        String newJwtToken = jwtTokenProvider.generateJwtToken(user);
        return new RefreshTokenResponseDto(newRawRefreshToken, newJwtToken);
    }

}
