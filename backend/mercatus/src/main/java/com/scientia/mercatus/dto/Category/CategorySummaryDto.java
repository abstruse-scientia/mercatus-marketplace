package com.scientia.mercatus.dto.Category;

public record CategorySummaryDto(long categoryId,
                                 String categoryName,
                                 String slug,
                                 int productCount) {
}
