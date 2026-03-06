package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Product.ProductImage.AddProductImageRequestDto;
import com.scientia.mercatus.dto.Product.ProductImage.UpdateProductImageRequestDto;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.entity.ProductImage;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.repository.ProductImageRepository;
import com.scientia.mercatus.service.IProductImageService;
import com.scientia.mercatus.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class    ProductImageServiceImpl implements IProductImageService {

    private final IProductService productService;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public ProductImage addImageToProduct(Long productId, AddProductImageRequestDto addImage) {
        if (addImage.getUrl() == null || addImage.getUrl().isEmpty()) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Image url cannot be empty");
        }
        if (addImage.getSortOrder() == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST,"Sort order cannot be empty.");
        }
        if (productId == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Product ID cannot be null.");
        }
        Product product = productService.getActiveProduct(productId);

        /*
        if (Boolean.TRUE.equals(addImage.getIsPrimary())) {
                    productImageRepository.clearPrimaryImage(productId);
        }
        fix: possible race condition when two thread clears primary image. Locking images with same productId
               will solve the issue.
         */
        productImageRepository.lockImagesForProduct(productId);


        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setUrl(addImage.getUrl());
        productImage.setSortOrder(addImage.getSortOrder());

        if(Boolean.TRUE.equals(addImage.getIsPrimary())){
            productImageRepository.clearPrimaryImage(productId);
            productImage.setIsPrimary(Boolean.TRUE);
        }else {
            productImage.setIsPrimary(Boolean.FALSE);
        }
        return productImageRepository.save(productImage);
    }

    @Override
    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        if (imageId == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Image ID cannot be null.");
        }
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(() ->
                new BusinessException(ErrorEnum.PRODUCT_IMAGE_NOT_FOUND));
        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new BusinessException(ErrorEnum.PRODUCT_ID_MISMATCH);
        }
        productImageRepository.lockImagesForProduct(productImage.getProduct().getProductId());
        boolean imageIsPrimary = Boolean.TRUE.equals(productImage.getIsPrimary());
        productImageRepository.delete(productImage);
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
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Product id cannot be null.");
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
            throw new BusinessException(ErrorEnum.INVALID_REQUEST);
        }
        List<ProductImage> images = productImageRepository.findByProductProductIdOrderBySortOrderAsc(productId);
        if (images == null || images.isEmpty()) {
            throw new BusinessException(ErrorEnum.PRODUCT_IMAGE_NOT_FOUND);
        }
        if (images.size() != imageIds.size()) {
            throw new BusinessException(ErrorEnum.IMAGE_COUNT_MISMATCH);
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
                throw new BusinessException(ErrorEnum.PRODUCT_IMAGE_NOT_FOUND);
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
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Product id cannot be null.");
        }
        if (imageId == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Image id cannot be null.");
        }

        ProductImage productImage = productImageRepository.findById(imageId).
                orElseThrow(()-> new BusinessException(ErrorEnum.PRODUCT_IMAGE_NOT_FOUND));
        productImageRepository.lockImagesForProduct(productId);
        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new BusinessException(ErrorEnum.PRODUCT_ID_MISMATCH, "Product Image's product Id does not match.");
        }
        if (Boolean.TRUE.equals(productImage.getIsPrimary())) {
            return;
        }
        productImageRepository.clearPrimaryImage(productId);
        productImage.setIsPrimary(Boolean.TRUE);

    }

    @Override
    @Transactional
    public ProductImage updateImage(Long productId, Long imageId, UpdateProductImageRequestDto updateProduct) {
        if (imageId == null) {
            throw new BusinessException(ErrorEnum.INVALID_REQUEST, "Image id cannot be null.");
        }

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorEnum.PRODUCT_IMAGE_NOT_FOUND));

        if (!productImage.getProduct().getProductId().equals(productId)) {
            throw new BusinessException(ErrorEnum.IMAGE_PRODUCT_MISMATCH);
        }

        productImageRepository.lockImagesForProduct(productId);


        if (updateProduct.getSortOrder() != null) {
            productImage.setSortOrder(updateProduct.getSortOrder());
        }

        Boolean newPrimary = updateProduct.getIsPrimary();
        //Case 1: auto promote - when newPrimary: false
        if(Boolean.FALSE.equals(newPrimary) && Boolean.TRUE.equals(productImage.getIsPrimary())) {
            ProductImage replacementImage = productImageRepository
                    .findReplacementImage(productId, productImage.getId())
                    .orElseThrow(() -> new BusinessException(ErrorEnum.PRODUCT_IMAGE_NOT_FOUND));
            replacementImage.setIsPrimary(true);
            productImage.setIsPrimary(false);

        }


        //Case 2: Normal promotion
        if (Boolean.TRUE.equals(newPrimary) && !productImage.getIsPrimary()) {
            productImageRepository.clearPrimaryImage(productId);
            productImage.setIsPrimary(true);
        }
        return productImage;
    }
}
