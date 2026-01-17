package com.scientia.mercatus.dto;

import java.math.BigDecimal;

public record UpdateQuantityRequestDto(Long productId, Integer quantity) {
}
