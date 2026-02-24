package com.scientia.mercatus.dto.Product.ProductImage;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductImageRequestDto {

    @NotBlank
    private String url;

    private Boolean isPrimary;

    private Integer sortOrder;
}
