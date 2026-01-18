package com.scientia.mercatus.config;

import com.scientia.mercatus.web.resolver.CartContextArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CartContextArgumentResolver cartContextArgumentResolver;

    public WebConfig(CartContextArgumentResolver cartContextArgumentResolver) {
        this.cartContextArgumentResolver = cartContextArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(cartContextArgumentResolver);
    }
}
