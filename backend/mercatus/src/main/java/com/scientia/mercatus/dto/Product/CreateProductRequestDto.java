package com.scientia.mercatus.dto.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequestDto {

    @NotBlank
    private String name;

    private String description;


    @NotNull
    private BigDecimal price;

    @NotBlank
    private String sku;


    @NotNull
    private Long categoryId;
}
