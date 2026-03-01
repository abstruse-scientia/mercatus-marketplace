package com.scientia.mercatus.config;

public final class PublicEndPoints {


    private PublicEndPoints(){}

    public static final String[] AUTH = {
            "/api/v1/auth/**"
    };

    public static final String[] WEBHOOKS = {
            "/api/v1/webhooks/**"
    };

    public static final String[] PRODUCTS = {
            "/api/v1/products/**"
    };



}
