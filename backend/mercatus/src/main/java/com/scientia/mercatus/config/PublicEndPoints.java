package com.scientia.mercatus.config;

public final class PublicEndPoints {


    private PublicEndPoints(){}

    public static final String[] AUTH = {
            "api/v1/auth/**"
    };

    public static final String[] WEBHOOKS = {
            "api/v1/webhook/**"
    };

    public static final String[] PRODUCTS = {
            "api/v1/products/{id}",
            "api/v1/products"
    };

    public static final String[] CART = {
            "api/v1/cart/**"
    };

}
