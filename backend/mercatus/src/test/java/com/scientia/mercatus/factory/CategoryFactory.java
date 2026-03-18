package com.scientia.mercatus.factory;

import com.scientia.mercatus.entity.Category;

import java.util.concurrent.atomic.AtomicInteger;

public class CategoryFactory {

    private static final AtomicInteger counter = new AtomicInteger(1);
    public static Category create() {
        Category category = new Category();
        category.setSlug("slug-" + counter.getAndIncrement());
        category.setCategoryName("name-" + counter.getAndIncrement());
        return category;
    }
}
