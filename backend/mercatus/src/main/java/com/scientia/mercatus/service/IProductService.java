package com.scientia.mercatus.service;


import com.scientia.mercatus.dto.Product.CreateProductRequestDto;
import com.scientia.mercatus.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    Product createProduct(CreateProductRequestDto createProductRequestDto);

    void deactivateProduct(Long productId);


    Product getActiveProduct(Long productId);

    Product getActiveProductBySku(String sku);

    Page<Product> listActiveProducts(Pageable pageable);


}
