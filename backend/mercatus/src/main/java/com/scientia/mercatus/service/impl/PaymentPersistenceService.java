package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Payment;
import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.entity.PaymentStatus;
import com.scientia.mercatus.repository.PaymentRepository;
import com.scientia.mercatus.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentPersistenceService {

    private final PaymentRepository paymentRepository;
    private final IOrderService orderService;

    @Transactional
    public String persistPaymentSuccess(PaymentProvider provider, String providerOrderId,
                                        String providerPaymentId, long amountReceived){
        Optional<Payment> optionalPayment = paymentRepository.findFirstByProviderAndProviderOrderIdAndStatusOrderByCreatedAtDesc(
                provider, providerOrderId, PaymentStatus.PENDING
        );
        if (optionalPayment.isEmpty()) {
            return null; //Duplicate webhook
        }

        Payment payment = optionalPayment.get();
        if (amountReceived != payment.getAmountExpected()) {
            throw new IllegalStateException("Amount received is not equal to amount expected");
        }
        payment.setProviderPaymentId(providerPaymentId);
        payment.setAmountReceived(amountReceived);
        payment.setStatus(PaymentStatus.SUCCESS);
        orderService.markPaid(payment.getOrderReference());

        return payment.getOrderReference();

    }



    @Transactional
    public String persistPaymentFailure(PaymentProvider provider,
                                        String providerOrderId,
                                        String providerPaymentId) {

        Optional<Payment> optionalPayment =
                paymentRepository
                        .findFirstByProviderAndProviderOrderIdAndStatusOrderByCreatedAtDesc(
                                provider,
                                providerOrderId,
                                PaymentStatus.PENDING
                        );

        // Idempotent duplicate webhook
        if (optionalPayment.isEmpty()) {
            return null;
        }

        Payment payment = optionalPayment.get();

        // Persist
        payment.setProviderPaymentId(providerPaymentId);
        payment.setStatus(PaymentStatus.FAILED);

        // Update order payment state (NOT lifecycle state)
        orderService.markPaymentFail(payment.getOrderReference());

        return payment.getOrderReference();
    }
}
