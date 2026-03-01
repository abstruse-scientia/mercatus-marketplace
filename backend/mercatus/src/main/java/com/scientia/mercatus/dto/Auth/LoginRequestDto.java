package com.scientia.mercatus.dto.Auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Email required.") String userEmail,
        @NotBlank(message = "Password required.") String password) {
}
