package com.scientia.mercatus.dto.Product.Admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminProductResponseDto {

    private Long productId;

    private String name;
    private BigDecimal price;
    private String sku;
    private String slug;

    private String primaryImageUrl;

    private Long categoryId;
    private String categoryName;

    private boolean isActive;


}
