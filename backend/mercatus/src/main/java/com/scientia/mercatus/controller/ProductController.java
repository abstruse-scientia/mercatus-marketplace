package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Product.ProductResponseDto;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.service.IProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {



    @Value("${pagination.max-size:10}")
    private int maxPageSize;

    private final IProductService productService;

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchActiveProducts(@RequestParam String query, Pageable pageable) {
        Pageable safePageable = getPageable(pageable);
        Page<Product> pageProduct = productService.searchActiveProductsByNameOrSlug(query,safePageable);
        return ResponseEntity.status(HttpStatus.OK).body(pageProduct.map(ProductResponseDto::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        Product product = productService.getActiveProduct(id);
        return ResponseEntity.ok(ProductResponseDto.from(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> listProducts(Pageable pageable) {
        Pageable safePageable = getPageable(pageable);
        return ResponseEntity.ok(
                productService.listActiveProducts(safePageable)
                        .map(ProductResponseDto::from)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDto>> listActiveProductsByCategory
            (@PathVariable Long categoryId, Pageable pageable) {
        Pageable safePageable = getPageable(pageable);
        Page<Product> pagedProduct =  productService.listActiveProductsByCategory(categoryId, safePageable);
        return ResponseEntity.status(HttpStatus.OK).body(pagedProduct.map(ProductResponseDto::from));
    }


    @GetMapping("category/name/{categoryName}")
    public ResponseEntity<Page<ProductResponseDto>> listActiveProductsByCategoryUsingCategoryName(
            @PathVariable String categoryName, Pageable pageable) {
        Pageable safePageable = getPageable(pageable);
        Page<Product> pagedProducts = productService.listActiveProductsByCategoryName(categoryName, safePageable);
        return ResponseEntity.status(HttpStatus.OK).body(pagedProducts.map(ProductResponseDto::from));

    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> listCategories() {
        List<String> categories = productService.listAllCategories();
        return ResponseEntity.ok(categories);
    }

    private Pageable getPageable(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), maxPageSize);
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                sort
        );
    }
}
