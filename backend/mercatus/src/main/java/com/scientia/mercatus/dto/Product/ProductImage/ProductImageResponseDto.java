package com.scientia.mercatus.dto.Product.ProductImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageResponseDto {
    long id;
    String url;
    int sortOrder;
    boolean isPrimary;
}
