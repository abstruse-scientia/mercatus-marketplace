package com.scientia.mercatus.service.impl;


import com.scientia.mercatus.dto.Payment.PaymentIntentResultDto;
import com.scientia.mercatus.entity.Payment;
import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.entity.PaymentStatus;
import com.scientia.mercatus.payment.provider.Stripe.StripeClient;
import com.scientia.mercatus.payment.provider.Stripe.StripePaymentIntent;
import com.scientia.mercatus.repository.PaymentRepository;
import com.scientia.mercatus.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StripePaymentGateway {

    private  final IPaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;

    public PaymentIntentResultDto createPaymentIntent(String orderReference, Long amountMinor, String currency) {
        Payment payment = paymentService.makePayment( // Create Internal Payment call
                orderReference,
                amountMinor,
                currency,
                PaymentProvider.STRIPE);


        StripePaymentIntent intent = stripeClient.createPaymentIntent(
                payment.getCurrency(), // Create Stripe Payment Intent (Mocked)
                payment.getId(),
                payment.getAmount());

        payment.setProviderPaymentId(intent.providerPaymentId());
        payment.setStatus(PaymentStatus.PROCESSING);

        paymentRepository.save(payment);
        return new PaymentIntentResultDto(payment.getId(), intent.clientSecret());

    }
}
