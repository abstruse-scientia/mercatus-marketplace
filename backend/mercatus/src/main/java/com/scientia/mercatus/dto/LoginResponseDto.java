package com.scientia.mercatus.dto;

import java.util.List;

public record LoginResponseDto(String message, List<String> roles, String token) {
}
