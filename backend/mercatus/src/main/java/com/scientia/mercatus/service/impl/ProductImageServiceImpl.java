package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Product.ProductImage.AddProductImageRequestDto;
import com.scientia.mercatus.dto.Product.ProductImage.UpdateProductImageRequestDto;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.entity.ProductImage;
import com.scientia.mercatus.repository.ProductImageRepository;
import com.scientia.mercatus.service.IProductImageService;
import com.scientia.mercatus.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements IProductImageService {

    private final IProductService productService;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public ProductImage addImageToProduct(Long productId, AddProductImageRequestDto addImage) {
        if (addImage.getUrl() == null || addImage.getUrl().isEmpty()) {
            throw new IllegalArgumentException("Image url cannot be empty.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null.");
        }
        Product product = productService.getActiveProduct(productId);
        if (Boolean.TRUE.equals(addImage.getIsPrimary())) {
            productImageRepository.clearPrimaryImage(productId);
        }
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setUrl(addImage.getUrl());
        productImage.setIsPrimary(Boolean.TRUE.equals(addImage.getIsPrimary()) ? true : null );
        productImage.setSortOrder(addImage.getSortOrder() != null ? addImage.getSortOrder() : 0);

        try {
            return productImageRepository.save(productImage);
        }catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Product already has primary image.", e);
        }
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        if (imageId == null) {
            throw new IllegalArgumentException("Image id cannot be null.");
        }
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(() ->
                new IllegalStateException("Image id not found."));
        boolean imageIsPrimary = Boolean.TRUE.equals(productImage.getIsPrimary());
        productImageRepository.deleteById(imageId);
        /* Promotion in case the deleted image was primary image
           find the first image of the product based on sort order and then promote
         */
        if (imageIsPrimary) {
            productImageRepository.findFirstByProductProductIdOrderBySortOrderAsc(
                    productImage.getProduct().getProductId()).ifPresent(
                            next-> next.setIsPrimary(true)
            );
        }
    }

    @Override
    public List<ProductImage> getImagesForProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null.");
        }
        return List.copyOf(productImageRepository.findByProductProductIdOrderBySortOrderAsc(productId));
    }

    @Override
    @Transactional
    public void reorderImages(Long productId, List<Long> imageIds) {

        /* How to reorder images?
        load all image related to the product. Check if their imageId matches the imageIds provided
         if match-> overwrite the sort order in sequence of the imageIds provided*/

        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null.");
        }
        List<ProductImage> images = productImageRepository.findByProductProductIdOrderBySortOrderAsc(productId);
        if (images.size() != imageIds.size()) {
            throw new IllegalStateException("Images count does not match.");
        }
        Map<Long, ProductImage> imageMap = images.stream().collect(Collectors.toMap(
                ProductImage::getId,//key
                Function.identity()//productImage-> productImage to set whole object as value
        ));
        int order = 1;
        for (Long imageId : imageIds) {
            ProductImage productImage = imageMap.get(imageId);
            if (productImage == null) {
                log.warn("Product image with id {} not found for product Id {}.", imageId,  productId);
                throw new IllegalStateException("Image id not found.");
            }
            productImage.setSortOrder(order++);
        }
    }

    @Override
    @Transactional
    public void setPrimaryImage(Long productId, Long imageId) {
        /* How to do this ? get the productImage associated with and set it to primary. before that check
        * if there exists primary id for this particular product deactivate it first and then set. */
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null.");
        }
        if (imageId == null) {
            throw new IllegalArgumentException("Image id cannot be null");
        }

        ProductImage productImage = productImageRepository.findById(imageId).
                orElseThrow(()-> new IllegalStateException("Image id not found."));
        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new IllegalStateException("Product id of image doesn't match.");
        }
        if (Boolean.TRUE.equals(productImage.getIsPrimary())) {
            return;
        }
        productImageRepository.clearPrimaryImage(productId);
        productImage.setIsPrimary(Boolean.TRUE);

    }

    @Override
    @Transactional
    public ProductImage updateImage(Long imageId, UpdateProductImageRequestDto updateProduct) {
        if (imageId == null) {
            throw new IllegalArgumentException("Image id cannot be null");
        }

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalStateException("Image not found"));

        Long productId = productImage.getProduct().getProductId();


        if (updateProduct.getSortOrder() != null) {
            productImage.setSortOrder(updateProduct.getSortOrder());
        }

        if (updateProduct.getIsPrimary() != null && updateProduct.getIsPrimary()) {

            if (!Boolean.TRUE.equals(productImage.getIsPrimary())) {
                productImageRepository.clearPrimaryImage(productId);
                productImage.setIsPrimary(true);
            }
        }
        if (updateProduct.getIsPrimary() != null && !updateProduct.getIsPrimary()) {
            productImage.setIsPrimary(false);
        }

        return productImage;
    }
}
