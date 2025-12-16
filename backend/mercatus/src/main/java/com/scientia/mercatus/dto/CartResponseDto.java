package com.scientia.mercatus.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDto(Long cartId, List<CartItemDto> items, int itemCount, BigDecimal subtotal) {
}
