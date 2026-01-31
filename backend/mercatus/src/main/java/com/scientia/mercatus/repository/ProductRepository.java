package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(Long productId);

    Optional<Product> findBySkuAndIsActiveTrue(String sku);

    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    boolean existsBySku(String sku);
}
