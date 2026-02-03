package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Payment;
import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.entity.PaymentStatus;
import com.scientia.mercatus.exception.InvalidPaymentStateException;
import com.scientia.mercatus.exception.PaymentAlreadyExistsException;
import com.scientia.mercatus.exception.PaymentNotFoundException;
import com.scientia.mercatus.repository.PaymentRepository;
import com.scientia.mercatus.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {


    private final PaymentRepository paymentRepository;

    @Override
    public Payment makePayment(String orderReference, Long amountMinor, String currency, PaymentProvider provider) {
        paymentRepository.findByOrderReference(orderReference)
                .ifPresent(p -> {
                    throw new PaymentAlreadyExistsException("Payment already exists for: " + orderReference);
                });

        Payment payment = new Payment();
        payment.setOrderReference(orderReference);
        payment.setAmount(amountMinor);
        payment.setCurrency(currency);
        payment.setProvider(provider);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setAttemptCount(0);

        return paymentRepository.save(payment);
    }

    @Override
    public void markPaymentFailed(String providerPaymentId) {

        Payment payment = paymentRepository
                .findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(providerPaymentId));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new InvalidPaymentStateException(
                    "Cannot fail an already SUCCESS payment"
            );
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setAttemptCount(payment.getAttemptCount() + 1);

    }

    @Override
    public void markPaymentSuccess(String providerPaymentId) {

        Payment payment = paymentRepository
                .findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(providerPaymentId));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return; // idempotent
        }

        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new InvalidPaymentStateException(
                    "Cannot mark FAILED payment as SUCCESS"
            );
        }

        payment.setStatus(PaymentStatus.SUCCESS);


    }
}
