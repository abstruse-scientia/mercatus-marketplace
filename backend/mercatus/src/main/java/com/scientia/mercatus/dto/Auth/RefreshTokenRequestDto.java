package com.scientia.mercatus.dto.Auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank(message = "Refresh Token required.") String refreshToken) {
}
