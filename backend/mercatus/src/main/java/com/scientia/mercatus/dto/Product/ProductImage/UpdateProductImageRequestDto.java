package com.scientia.mercatus.dto.Product.ProductImage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateProductImageRequestDto {
    boolean isPrimary;
    int sortOrder;
}
