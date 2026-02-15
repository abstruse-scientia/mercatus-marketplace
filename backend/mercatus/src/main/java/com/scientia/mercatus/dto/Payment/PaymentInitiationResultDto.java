package com.scientia.mercatus.dto.Payment;

import com.scientia.mercatus.entity.PaymentProvider;

public record PaymentInitiationResultDto(PaymentProvider paymentProvider, String orderId, long amount, String currency) {
}
