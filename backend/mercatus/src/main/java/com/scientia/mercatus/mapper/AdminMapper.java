package com.scientia.mercatus.mapper;

import com.scientia.mercatus.dto.Product.Admin.AdminProductResponseDto;
import com.scientia.mercatus.entity.Product;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMapper {

    public AdminProductResponseDto  toAdminProductResponseDto(Product product) {
        AdminProductResponseDto adminProductResponseDto = new AdminProductResponseDto();
        adminProductResponseDto.setProductId(product.getProductId());
        adminProductResponseDto.setName(product.getName());
        adminProductResponseDto.setPrice(product.getPrice());
        adminProductResponseDto.setCategoryId(product.getCategory().getCategoryId());
        adminProductResponseDto.setCategoryName(product.getCategory().getCategoryName());
        adminProductResponseDto.setPrimaryImageUrl(product.getPrimaryImageUrl());
        adminProductResponseDto.setSku(product.getSku());
        adminProductResponseDto.setIsActive(product.getIsActive());
        return adminProductResponseDto;
    }
}
