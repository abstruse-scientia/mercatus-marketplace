package com.scientia.mercatus.service;


import com.scientia.mercatus.dto.Product.Admin.CreateProductRequestDto;
import com.scientia.mercatus.dto.Product.Admin.UpdateProductRequestDto;
import com.scientia.mercatus.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    Product createProduct(CreateProductRequestDto createProductRequestDto);

    void deactivateProduct(Long productId);


    Product getActiveProduct(Long productId);

    Product getActiveProductBySku(String sku);

    Page<Product> listActiveProducts(Pageable pageable);


    Product updateProduct(Long productId, UpdateProductRequestDto update);
    Product reactivateProduct(Long productId);
    Product getProductById(Long productId);
    Page<Product> listAllProducts(Pageable pageable);
    Page<Product> listInactiveProducts(Pageable pageable);
    Page<Product> searchProducts(String query, Pageable pageable);
    Page<Product> listProductsByCategory(Long categoryId, Pageable pageable);






}
