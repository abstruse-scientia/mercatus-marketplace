package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Product.Admin.AdminProductResponseDto;
import com.scientia.mercatus.dto.Product.Admin.UpdateProductRequestDto;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.mapper.AdminMapper;
import com.scientia.mercatus.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    @Value("${pagination.max-size:10}")
    private int maxPageSize;

    private final IProductService productService;
    private final AdminMapper adminMapper;

    @GetMapping
    public ResponseEntity<Page<AdminProductResponseDto>> listProducts(Pageable pageable) {
        Pageable safePageable = getSafePageable(pageable);
        Page<Product> pagedProduct=  productService.listAllProducts(safePageable);
        Page<AdminProductResponseDto> pagedResponse= pagedProduct.map(adminMapper::toAdminProductResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminProductResponseDto> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(adminMapper.toAdminProductResponseDto(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminProductResponseDto> updateProduct
            (@PathVariable Long id, @Valid @RequestBody UpdateProductRequestDto updateProductRequestDto) {
        Product product = productService.updateProduct(id, updateProductRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(adminMapper.toAdminProductResponseDto(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<AdminProductResponseDto> reactivateProduct(@PathVariable Long id) {
        Product product = productService.reactivateProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(adminMapper.toAdminProductResponseDto(product));
    }

    @GetMapping("/inactive")
    public ResponseEntity<Page<AdminProductResponseDto>> listInactiveProducts(Pageable pageable) {
        Pageable safePageable = getSafePageable(pageable);
        Page<Product> pagedInactiveProduct = productService.listInactiveProducts(safePageable);
        Page<AdminProductResponseDto> pagedResponse = pagedInactiveProduct.map(adminMapper::toAdminProductResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AdminProductResponseDto>> searchProduct(@RequestParam  String query, Pageable pageable) {
        Pageable safePageable = getSafePageable(pageable);
        Page<Product> searchProducts = productService.searchProducts(query, safePageable);
        Page<AdminProductResponseDto> pagedResponse = searchProducts.map(adminMapper::toAdminProductResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<AdminProductResponseDto>> listProductsByCategory
            (@PathVariable Long categoryId, Pageable pageable) {
        Pageable safePageable = getSafePageable(pageable);
        Page<Product> categoryProducts = productService.listProductsByCategory(categoryId, safePageable);
        Page<AdminProductResponseDto> pagedResponse = categoryProducts.map(adminMapper::toAdminProductResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    private Pageable getSafePageable(Pageable pageable) {
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
