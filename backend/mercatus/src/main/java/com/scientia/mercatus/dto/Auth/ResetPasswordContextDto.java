package com.scientia.mercatus.dto.Auth;

import lombok.Getter;

public record ResetPasswordContextDto(
        String accessToken,
        String newPassword
) {
}
