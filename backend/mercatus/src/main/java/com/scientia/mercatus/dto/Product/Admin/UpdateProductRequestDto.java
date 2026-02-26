package com.scientia.mercatus.dto.Product.Admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductRequestDto {

    @NotBlank(message = "Name required.")
    private String name;

    private String description;

    @NotNull(message = "Price above zero required")
    @Positive
    private BigDecimal price;

    @NotBlank(message = "SKU required.")
    @Size(max = 100)
    private String sku;

    private String slug;

    @NotNull(message = "Category id required.")
    private Long categoryId;

    private String primaryImageUrl;
}
