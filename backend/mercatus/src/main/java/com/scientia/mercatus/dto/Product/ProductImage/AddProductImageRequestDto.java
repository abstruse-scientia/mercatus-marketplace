package com.scientia.mercatus.dto.Product.ProductImage;

import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class AddProductImageRequestDto {
    boolean isPrimary;
    long productId;
    String url;
    int sortOrder;
}
