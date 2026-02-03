package com.scientia.mercatus.payment.provider.Stripe;

public interface StripeClient {
    StripePaymentIntent createPaymentIntent(String currency, Long internalPaymentId, long amountMinor);
}
