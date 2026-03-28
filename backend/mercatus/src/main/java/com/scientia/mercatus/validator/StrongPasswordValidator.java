package com.scientia.mercatus.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    //Regex
    // Password must be at least 8 characters and contain:
    // - At least one uppercase letter
    // - At least one lowercase letter
    // - At least one digit
    // - At least one special character
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$"
    );

    @Override
    public void initialize(StrongPassword annotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true; // @NotBlank will validate this
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
}

