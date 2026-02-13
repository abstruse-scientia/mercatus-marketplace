package com.scientia.mercatus.dto.Payment;

import com.scientia.mercatus.entity.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatePaymentResponseDto {

    private String orderId;
    private long amount;
    private String currency;
    private PaymentProvider paymentProvider;
}
