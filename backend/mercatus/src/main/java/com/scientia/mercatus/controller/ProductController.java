package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Product.Admin.CreateProductRequestDto;
import com.scientia.mercatus.dto.Product.ProductResponseDto;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody CreateProductRequestDto request) {

        Product product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponseDto.from(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        Product product = productService.getActiveProduct(id);
        return ResponseEntity.ok(ProductResponseDto.from(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> listProducts(Pageable pageable) {
        return ResponseEntity.ok(
                productService.listActiveProducts(pageable)
                        .map(ProductResponseDto::from)
        );
    }
}
