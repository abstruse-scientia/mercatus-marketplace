package com.scientia.mercatus.dto;

import java.util.List;

public record LoginResponseDto(String message, UserDto user, String jwtToken) {
}
