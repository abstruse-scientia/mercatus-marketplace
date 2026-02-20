package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.Product.ProductImage.AddProductImageRequestDto;
import com.scientia.mercatus.dto.Product.ProductImage.UpdateProductImageRequestDto;
import com.scientia.mercatus.entity.ProductImage;

import java.util.List;

public interface IProductImageService {
    ProductImage addImageToProduct(Long productId, AddProductImageRequestDto addProduct);
    List<ProductImage> getImagesForProduct(Long productId);
    ProductImage updateImage(Long imageId, UpdateProductImageRequestDto updateProduct);
    void deleteImage(Long imageId);
    void setPrimaryImage(Long productId, Long imageId);
    void reorderImages(Long productId, List<Long> imageIds);
}
