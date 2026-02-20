package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Product.Admin.CreateProductRequestDto;
import com.scientia.mercatus.entity.Category;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.repository.CategoryRepository;
import com.scientia.mercatus.repository.ProductRepository;
import com.scientia.mercatus.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements IProductService {



    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Product createProduct(CreateProductRequestDto request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new BusinessException("SKU already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setIsActive(true);

        return productRepository.save(product);
    }

    @Transactional
    public void deactivateProduct(Long productId) {
        Product product = getActiveProduct(productId);
        product.setIsActive(false);
    }

    public Product getActiveProduct(Long productId) {
        return productRepository.findById(productId)
                .filter(Product::getIsActive)
                .orElseThrow(() -> new BusinessException("Active product not found"));
    }

    public Product getActiveProductBySku(String sku) {
        return productRepository.findBySkuAndIsActiveTrue(sku)
                .orElseThrow(() -> new BusinessException("Product not available"));
    }

    public Page<Product> listActiveProducts(Pageable pageable) {
        return productRepository.findAllByIsActiveTrue(pageable);
    }


}
