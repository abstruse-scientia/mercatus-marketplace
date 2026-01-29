package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "stock_reservation", schema = "mercatus_db", uniqueConstraints = {
        @UniqueConstraint(name = "reservation_key", columnNames = {"reservation_key"})
})
public class StockReservation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "reservation_key", nullable = false, length = 100, unique = true)
    private String reservationKey;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Size(max = 100)
    @NotNull
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;



    @Size(max = 255)
    @NotNull
    @Column(name = "order_reference", nullable = false, length = 255)
    private String orderReference;

}