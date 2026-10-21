package com.scientia.mercatus.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDto(
        @NotBlank(message = "Email required.")
        @Email(message = "Invalid email format.")
        String email
) {
}
