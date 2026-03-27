package com.scientia.mercatus.service.impl;

import com.scientia.mercatus.entity.Payment;
import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.entity.PaymentStatus;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;
import com.scientia.mercatus.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentPersistenceService {

    private final PaymentRepository paymentRepository;




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
        payment.setProviderPaymentId(providerPaymentId);
        payment.setAmountReceived(amountReceived);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.flush();
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


        return payment.getOrderReference();
    }
}
