package com.scientia.mercatus.dto.Product.Admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequestDto {

    @NotBlank(message = "Product name required.")
    private String name;

    private String description;


    @NotNull(message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "SKU is required")
    private String sku;


    @NotNull(message = "Category id required")
    private Long categoryId;
}
