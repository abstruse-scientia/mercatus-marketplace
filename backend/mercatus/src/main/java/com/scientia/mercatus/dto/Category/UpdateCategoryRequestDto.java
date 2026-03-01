package com.scientia.mercatus.dto.Category;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequestDto(
        @NotBlank(message = "Name required for category.") String categoryName,
        @NotBlank(message = "Slug required") String slug) {
}
