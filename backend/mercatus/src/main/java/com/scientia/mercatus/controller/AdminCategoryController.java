package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Category.CategoryResponseDto;
import com.scientia.mercatus.dto.Category.CreateCategoryRequestDto;
import com.scientia.mercatus.dto.Category.UpdateCategoryRequestDto;

import com.scientia.mercatus.entity.Category;

import com.scientia.mercatus.service.ICategoryService;
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
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCategoryController {


    @Value("${pagination.max-size:10}")
    private int maxPageSize;


    private final ICategoryService categoryService;


    @GetMapping
    public ResponseEntity<Page<CategoryResponseDto>> getCategories(Pageable pageable) {
        int size = Math.min(maxPageSize, pageable.getPageSize());
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable safePageable =  PageRequest.of(pageable.getPageNumber(), size, sort);
        Page<Category> categoryResponse = categoryService.listCategories(safePageable);
        Page<CategoryResponseDto> responseDto = categoryResponse.map(this::toCategoryResponseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body(toCategoryResponseDto(category));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryRequestDto createDto) {
        Category category = categoryService.createCategory(createDto.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(toCategoryResponseDto(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory
            (@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto) {
        Category category = categoryService.updateCategory(id, updateCategoryRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(toCategoryResponseDto(category));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
         categoryService.deleteCategory(id);
         return ResponseEntity.noContent().build();
    }



    private CategoryResponseDto toCategoryResponseDto(Category category) {
        return  new CategoryResponseDto(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getSlug()
        );
    }
}