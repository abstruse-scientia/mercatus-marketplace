package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Long, Product> {
}
