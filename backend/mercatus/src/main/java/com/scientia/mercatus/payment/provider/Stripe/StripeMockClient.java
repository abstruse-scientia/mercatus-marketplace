package com.scientia.mercatus.payment.provider.Stripe;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StripeMockClient implements StripeClient {


    @Override
    public StripePaymentIntent createPaymentIntent(String currency, Long internalPaymentId, long amountMinor) {
        return new StripePaymentIntent(
                "pi_mock_" + internalPaymentId,
                "cs_mock_" + UUID.randomUUID()
        );
    }
}
