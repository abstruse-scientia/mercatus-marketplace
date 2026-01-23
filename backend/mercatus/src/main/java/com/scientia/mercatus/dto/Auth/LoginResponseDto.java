package com.scientia.mercatus.dto.Auth;

import com.scientia.mercatus.dto.UserDto;

public record LoginResponseDto(String message, UserDto user, String jwtToken, String refreshToken) {
}
