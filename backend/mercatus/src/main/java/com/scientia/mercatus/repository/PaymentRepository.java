package com.scientia.mercatus.repository;

import com.scientia.mercatus.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderReference(String orderReference);

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
}
