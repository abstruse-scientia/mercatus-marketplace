package com.scientia.mercatus.service;


import com.scientia.mercatus.dto.Product.Admin.CreateProductRequestDto;
import com.scientia.mercatus.dto.Product.Admin.UpdateProductRequestDto;
import com.scientia.mercatus.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {


    // public
    Page<Product> searchActiveProductsByNameOrSlug(String query, Pageable pageable);

    // public
    Product getActiveProduct(Long productId);

    //don't know sku is kind of internal tracking unit
    Product getActiveProductBySku(String sku);

    // public
    Page<Product> listActiveProducts(Pageable pageable);
    //public
    Page<Product> listActiveProductsByCategory(Long categoryId, Pageable pageable);


    // update belongs to admin
    Product updateProduct(Long productId, UpdateProductRequestDto update);
    //admin
    Product reactivateProduct(Long productId);
    // admin
    Product getProductById(Long productId);
    //admin
    Page<Product> listAllProducts(Pageable pageable);
    //admin
    Page<Product> listInactiveProducts(Pageable pageable);

    //admin
    Product createProduct(CreateProductRequestDto createProductRequestDto);
    //admin
    void deactivateProduct(Long productId);

    Page<Product> listProductsByCategory(Long categoryId, Pageable pageable);




    /*
     Need to fix the list: By reorganizing what should be controlled by admin , and what should
     be client related.
     */

}
