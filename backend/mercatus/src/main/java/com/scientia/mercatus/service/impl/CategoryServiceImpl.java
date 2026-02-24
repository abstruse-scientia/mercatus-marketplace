package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.dto.Category.UpdateCategoryRequestDto;
import com.scientia.mercatus.entity.Category;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.repository.CategoryRepository;
import com.scientia.mercatus.service.ICategoryService;
import com.scientia.mercatus.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final SlugUtil slugUtil;

    @Override
    @Transactional
    public Category createCategory(String categoryName) {
        String name = validateName(categoryName);
        if (categoryRepository.existsByCategoryNameIgnoreCase(name)) {
            throw new BusinessException("Category already exists");
        }
        String slug = slugUtil.baseSlug(name);
        Category newCategory =  new Category();
        newCategory.setCategoryName(categoryName);
        newCategory.setSlug(slug);
        try {
            return categoryRepository.save(newCategory);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Category already exists");
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Long id = validateId(categoryId);
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new BusinessException("Category not found"));
        if (categoryRepository.hasProducts(categoryId)) {
            log.warn("Category {} has products attached to it",  categoryId);
            throw new BusinessException("Category with products can not be deleted");
        }
        categoryRepository.delete(category);
    }

    @Override
    public Category getCategory(Long categoryId) {

        Long id = validateId(categoryId);
        return categoryRepository.findById(id).orElseThrow(() -> new BusinessException("Category not found"));
    }

    @Override
    public Category getCategoryBySlug(String slug) {

        String validatedSlug = validateSlug(slug);
        return categoryRepository.findBySlug(validatedSlug)
                .orElseThrow(() -> new BusinessException("Category not found"));
    }



    @Override
    public Page<Category> listCategories(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, UpdateCategoryRequestDto updateCategoryRequestDto) {
        Long id = validateId(categoryId);
        if (updateCategoryRequestDto == null) {
            throw new IllegalArgumentException("UpdateCategoryRequestDto cannot be null");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));
        if (updateCategoryRequestDto.categoryName() != null && updateCategoryRequestDto.categoryName().isBlank()) {
            String newName = updateCategoryRequestDto.categoryName().trim();
            if (categoryRepository.existsByCategoryNameIgnoreCase(newName)) {
                throw new BusinessException("Category name already exists");
            }
            category.setCategoryName(newName);
        }
        return category;

    }


    /* -------------------- Helper functions --------------------   */
    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Category name cannot be blank");
        }
        return name.trim();
    }

    private Long validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }
        return id;
    }

    private  String validateSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new BusinessException("Slug cannot be blank");
        }
        return slug.trim();
    }

}
