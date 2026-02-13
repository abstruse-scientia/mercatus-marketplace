package com.scientia.mercatus.service;

import com.razorpay.RazorpayException;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.PaymentProvider;

public interface IPaymentService {

    PaymentInitiationResultDto initiatePayment(String orderReference, String currency, PaymentProvider provider);

    void markPaymentSuccess(PaymentProvider provider, String providerOrderId,
                            String providerPaymentId, long amountReceived);

    void markPaymentFailed(PaymentProvider provider, String providerOderId, String providerPaymentId);
}
