package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Product.ProductImage.AddProductImageRequestDto;
import com.scientia.mercatus.dto.Product.ProductImage.ProductImageResponseDto;
import com.scientia.mercatus.dto.Product.ProductImage.UpdateProductImageRequestDto;
import com.scientia.mercatus.entity.ProductImage;
import com.scientia.mercatus.service.IProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products/{productId}/images")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductImageController {

    private final IProductImageService productImageService;

    @GetMapping
    public ResponseEntity<List<ProductImageResponseDto>> getImages(@PathVariable Long productId) {
        List<ProductImageResponseDto> images = productImageService.getImagesForProduct(productId)
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(images);
    }

    @PostMapping
    public ResponseEntity<ProductImageResponseDto> addImage(
            @PathVariable Long productId,
            @Valid @RequestBody AddProductImageRequestDto request) {
        ProductImage image = productImageService.addImageToProduct(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(image));
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<ProductImageResponseDto> updateImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @RequestBody @Valid UpdateProductImageRequestDto request) {
        ProductImage image = productImageService.updateImage(productId, imageId, request);
        return ResponseEntity.ok(toDto(image));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productImageService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{imageId}/primary")
    public ResponseEntity<Void> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productImageService.setPrimaryImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderImages(
            @PathVariable Long productId,
            @RequestBody List<Long> imageIds) {
        productImageService.reorderImages(productId, imageIds);
        return ResponseEntity.noContent().build();
    }

    private ProductImageResponseDto toDto(ProductImage image) {
        ProductImageResponseDto dto = new ProductImageResponseDto();
        dto.setId(image.getId());
        dto.setUrl(image.getUrl());
        dto.setSortOrder(image.getSortOrder());
        dto.setPrimary(image.getIsPrimary());
        return dto;
    }
}

