package com.scientia.mercatus.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatePaymentResponseDto {

    private Long paymentId;
    private String clientSecret;
}
