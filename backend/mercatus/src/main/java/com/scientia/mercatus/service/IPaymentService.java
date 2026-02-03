package com.scientia.mercatus.service;

import com.scientia.mercatus.entity.Payment;
import com.scientia.mercatus.entity.PaymentProvider;

public interface IPaymentService {

    Payment makePayment(String orderReference, Long amountMinor, String currency, PaymentProvider provider);

    void markPaymentSuccess(String providerPaymentId);

    void markPaymentFailed(String providerPaymentId);
}
