package com.scientia.mercatus.builder;

import com.scientia.mercatus.entity.Category;
import com.scientia.mercatus.entity.Product;
import com.scientia.mercatus.factory.CategoryFactory;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductBuilder {

    private String name = "Nikon fuji";
    private BigDecimal price = BigDecimal.valueOf(1000);
    private String slug = "product-" + UUID.randomUUID();
    private String sku  = "sku-" + UUID.randomUUID();
    private Boolean isActive = Boolean.TRUE;

    private Category category = CategoryFactory.create();
    private String description;
    private String primaryImageUrl;




    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withSku(String sku) {
        this.sku = sku;
        return this;
    }

    public ProductBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public ProductBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder withPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
        return this;
    }


    public Product build() {

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setSlug(slug);
        product.setSku(sku);
        product.setIsActive(isActive);
        product.setCategory(category);
        product.setPrimaryImageUrl(primaryImageUrl);
        product.setDescription(description);
        return  product;
    }
}
