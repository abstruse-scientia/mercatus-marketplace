package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Product.Admin.CreateProductRequestDto;
import com.scientia.mercatus.dto.Product.Admin.UpdateProductRequestDto;
import com.scientia.mercatus.entity.Category;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.repository.CategoryRepository;
import com.scientia.mercatus.repository.ProductRepository;
import com.scientia.mercatus.service.IProductService;
import com.scientia.mercatus.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements IProductService {


    private static final int MAX_SLUG_GENERATION = 5;

    private final SlugUtil slugUtil;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;



    /* ------------------------Public services----------------------------------*/


    /*
        Cross service method.
     */
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
        Pageable validatePageable = validatePageable(pageable);
        return productRepository.findAllByIsActiveTrue(validatePageable);
    }


    @Override
    public Page<Product> searchProducts(String query, Pageable pageable) {
        if (query == null || query.isEmpty()) {
            throw new BusinessException("Query cannot be null or empty");
        }
        Pageable validatedPageable = validatePageable(pageable);
        return productRepository.searchByNameOrSkuAndIsActive(query, validatedPageable);
    }


    /* --------------------------------------Admin services -----------------------------*/

    @Transactional
    @Override
    public Product createProduct(CreateProductRequestDto request) {


        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));

        Product product = mapToProduct(request,  category);

        try {
            return productRepository.save(product);
        }catch(DataIntegrityViolationException e) {
            throw new BusinessException("Product already exists");
        }
    }


    @Transactional
    @Override
    public void deactivateProduct(Long productId) {
        Product product = getProductForUpdate(productId);
        if (!product.getIsActive()) {
            return;
        }
        product.setIsActive(false);
    }


    @Override
    @Transactional
    public Product reactivateProduct(Long productId) {
        Product product = getProductForUpdate(productId);
        if (product.getIsActive()) {
            return product;
        }
        product.setIsActive(true);
        return product;
    }

    @Override
    public Product getProductById(Long productId) {
        validateProductId(productId);
        return  productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Product not found"));
    }

    @Override
    public Page<Product> listProductsByCategory(Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            throw new BusinessException("Category id cannot be null.");
        }
        Pageable validatedPageable = validatePageable(pageable);

        return productRepository.findByCategoryCategoryId(categoryId, validatedPageable);
    }

    @Override
    public Page<Product> listAllProducts(Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);
        return productRepository.findAll(validatedPageable);
    }

    @Override
    public Page<Product> listInactiveProducts(Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);
        return productRepository.findAllByIsActiveFalse(validatedPageable);
    }



    @Override
    @Transactional
    public Product updateProduct(Long productId, UpdateProductRequestDto update) {
        validateProductId(productId);

        Product product = productRepository.findProductForUpdate(productId)
                .orElseThrow(()-> new BusinessException("Product not found"));

        if (update.getName() != null && !update.getName().equals(product.getName())) {
            product.setName(update.getName());
            product.setSlug(slugForUpdate(update.getName(), productId));
        }
        if (update.getDescription() != null) {
            product.setDescription(update.getDescription());
        }
        if (update.getPrice() != null) {
            product.setPrice(update.getPrice());
        }
        if (update.getCategoryId() != null) {
            Category category = categoryRepository.findById(update.getCategoryId())
                    .orElseThrow(() -> new BusinessException("Category not found"));
            product.setCategory(category);
        }
        if (update.getPrimaryImageUrl() != null) {
            product.setPrimaryImageUrl(update.getPrimaryImageUrl());
        }
        return product;
    }




    /*
        -----------------------Helper functions -------------------------------------------
     */
    void  validateProductId(Long productId) {
        if (productId == null) {
            throw new BusinessException("Product id cannot be null");
        }
    }

    private Pageable validatePageable(Pageable pageable) {
        if (pageable == null) {
            throw new BusinessException("Pageable cannot be null");
        }
        return pageable;
    }

    private Product mapToProduct(CreateProductRequestDto request, Category category) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setIsActive(true);
        String slug = slugForCreate(request.getName());
        product.setSlug(slug);

        return product;
    }

    private String slugForUpdate(String name, Long productId) {
        String baseSlug = slugUtil.baseSlug(name);
        String productSlug = baseSlug;
        for (int attempt = 1; attempt <= MAX_SLUG_GENERATION; attempt++) {
            if (!productRepository.existsBySlugAndProductIdNot(productSlug, productId)) {
                return productSlug;
            }
            productSlug = baseSlug + "-" + attempt;
        }
        throw new BusinessException("Unable to generate unique slug");
    }

    private String slugForCreate(String name) {
        String baseSlug = slugUtil.baseSlug(name);
        String productSlug = baseSlug;
        for (int attempt = 1; attempt <= MAX_SLUG_GENERATION; attempt++) {
            if (!productRepository.existsBySlug(productSlug)) {
                return productSlug;
            }
            productSlug = baseSlug + "-" + attempt;
        }
        throw new BusinessException("Unable to generate unique slug");
    }

    private Product getProductForUpdate(Long productId) {
        validateProductId(productId);
        return productRepository.findProductForUpdate(productId)
                .orElseThrow(()-> new BusinessException("Product not found"));
    }
}
