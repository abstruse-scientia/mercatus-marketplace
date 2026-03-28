package com.scientia.mercatus.dto.Auth;

import com.scientia.mercatus.validator.PasswordMatch;
import com.scientia.mercatus.validator.StrongPassword;
import com.scientia.mercatus.validator.UniqueEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@PasswordMatch(message = "Passwords do not match")
public record RegisterRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @UniqueEmail(message = "Email is already registered")
        String email,

        @NotBlank(message = "Password is required")
        @StrongPassword(message = "Password must be at least 8 characters and contain uppercase, " +
                "lowercase, number, and special character")
        String password,

        @NotBlank(message = "Password confirmation is required")
        String confirmPassword,

        @NotBlank(message = "Username is required")
        @Size(min = 2, max = 100, message = "Username must be between 2 and 100 characters")
        String userName
) {
}

