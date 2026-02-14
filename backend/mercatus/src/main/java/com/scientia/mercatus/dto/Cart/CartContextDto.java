package com.scientia.mercatus.dto.Cart;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class CartContextDto {
    @NotNull
    private final String sessionId;

    @NotNull
    private final Long userId;

    public CartContextDto(String sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

}
