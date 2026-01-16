package com.scientia.mercatus.dto;

import lombok.Getter;


@Getter
public class CartContextDto {
    private final String sessionId;
    private final Long userId;

    public CartContextDto(String sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

}
