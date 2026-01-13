package com.scientia.mercatus.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record CartResponseDto(Set<CartItemDto> items, int itemCount, BigDecimal subtotal) {
}
