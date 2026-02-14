package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "order_reference", nullable = false)
    private String orderReference;

    @Column(name = "amount_received")
    private Long amountReceived;

    @NotNull
    @Column(name = "amount_expected", nullable = false)
    private Long amountExpected;

    @Size(max = 3)
    @NotNull
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 40)
    private PaymentProvider provider;

    @Size(max = 255)
    @Column(name = "provider_payment_id", unique = true)
    private String providerPaymentId;

    @Size(max = 255)
    @Column(name = "provider_order_id")
    private String providerOrderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private PaymentStatus status;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
}