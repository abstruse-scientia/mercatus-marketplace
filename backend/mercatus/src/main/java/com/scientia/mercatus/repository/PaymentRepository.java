package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Payment;
import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderReference(String orderReference);

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

    Optional<Payment> findFirstByProviderAndProviderOrderIdAndStatusOrderByCreatedAtDesc(PaymentProvider provider,
                                                                               String providerOrderId,
                                                                               PaymentStatus paymentStatus);

    Optional<Payment> findByOrderReferenceAndStatus(String orderReference, PaymentStatus paymentStatus);

    Optional<Payment> findFirstByProviderAndProviderOrderIdOrderByIdDesc(
            PaymentProvider provider,
            String providerOrderId
    );
}
