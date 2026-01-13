package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "mercatus_db", uniqueConstraints = {
        @UniqueConstraint(name = "UK2mnxs4cfnjg2w7q5xw77x91u", columnNames = {"order_reference"})
})
public class Orders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;



    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 50)
    @NotNull
    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus;

    @Size(max = 100)
    @NotNull
    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Size(max = 255)
    @NotNull
    @Column(name = "order_reference", nullable = false)
    private String orderReference;

    @OneToMany
    @JoinColumn(name = "order_id")
    private Set<OrderItems> orderItems = new LinkedHashSet<>();

}