package com.scientia.mercatus.dto.Order;

public record OrderItemSummaryDto(
        Long productId,
        String productName,
        String primaryImageUrl
) {
}
