package com.scientia.mercatus.dto.Category;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequestDto(@NotBlank(message = "Name required for category.") String name) {
}
