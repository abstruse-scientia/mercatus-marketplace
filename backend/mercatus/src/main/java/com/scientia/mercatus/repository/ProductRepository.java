package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(Long productId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.productId = :productId")
    Optional<Product> findProductForUpdate(@Param("productId")Long productId);


    Optional<Product> findBySkuAndIsActiveTrue(String sku);

    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    boolean existsBySku(String sku);

    Page<Product> findAllByIsActiveFalse(Pageable pageable);


    Page<Product> findByCategoryCategoryId(Long categoryId, Pageable validatedPageable);

    @Query("""
            select p from Product p where lower(p.name) like lower(concat('%' , :query, '%'))
            or lower(p.sku) like lower(concat('%', :query , '%'))
            and p.isActive = true
    """)
    Page<Product> searchByNameOrSlugAndIsActive(@Param("query")String query, Pageable pageable);

    boolean existsBySlugAndProductIdNot(String slug,  Long productId);

    boolean existsBySlug(String slug);

    @Query("""
        select p from Product p where p.category.categoryId = :categoryId and p.isActive = true
    """)
    Page<Product> findActiveProductsByCategory(@Param("categoryId")Long categoryId, Pageable pageable);
}

