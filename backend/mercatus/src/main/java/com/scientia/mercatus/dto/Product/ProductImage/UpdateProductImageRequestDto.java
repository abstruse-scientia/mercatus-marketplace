package com.scientia.mercatus.dto.Product.ProductImage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProductImageRequestDto {
    private  Boolean isPrimary;
    private  Integer sortOrder;
}
