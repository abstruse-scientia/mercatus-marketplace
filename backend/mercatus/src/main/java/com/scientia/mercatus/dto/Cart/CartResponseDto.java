package com.scientia.mercatus.dto.Cart;

import java.math.BigDecimal;
import java.util.Set;

public record CartResponseDto(Set<CartItemDto> items, int itemCount, BigDecimal subtotal) {
}
