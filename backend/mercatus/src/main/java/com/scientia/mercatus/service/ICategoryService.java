package com.scientia.mercatus.service;

import com.scientia.mercatus.dto.Category.UpdateCategoryRequestDto;
import com.scientia.mercatus.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {

    Category createCategory(String categoryName);
    Category updateCategory(Long categoryId, UpdateCategoryRequestDto updateCategoryRequestDto);
    void deleteCategory(Long categoryId);
    Category getCategory(Long categoryId);
    Category getCategoryBySlug(String slug);
    Page<Category> listCategories(Pageable pageable);
}
