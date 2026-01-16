package com.scientia.mercatus.dto;

import java.math.BigDecimal;

public record AddToCartRequestDto(Long productId, BigDecimal quantity) {
}
