package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {


    // Find all images for a product
    List<ProductImage> findByProductProductId(Long productId);


    // Find all images for a product ordered by sort order
    List<ProductImage> findByProductProductIdOrderBySortOrderAsc(Long productId);


    //Find the primary image for a product
    Optional<ProductImage> findByProductProductIdAndIsPrimaryTrue(Long productId);


    // Delete all images for a product
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId")
    void deleteByProductProductId(@Param("productId") Long productId);


    // Count images for a product
    Long countByProductProductId(Long productId);


    // Check if a product has any images
    boolean existsByProductProductId(Long productId);

    // Find all images by product IDs for bulk operations
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId IN :productIds ORDER BY pi.product.productId, pi.sortOrder")
    List<ProductImage> findByProductProductIdIn(@Param("productIds") List<Long> productIds);


    // Reset primary flag or all images of a product
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.productId = :productId")
    void resetPrimaryFlagForProduct(@Param("productId") Long productId);


    // Get the maximum sort order for a product's images
    @Query("SELECT COALESCE(MAX(pi.sortOrder), 0) FROM ProductImage pi WHERE pi.product.productId = :productId")
    Integer findMaxSortOrderByProductId(@Param("productId") Long productId);
}

