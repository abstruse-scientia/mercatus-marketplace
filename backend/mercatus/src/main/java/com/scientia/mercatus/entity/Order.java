package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Size(max = 50)
    @NotNull
    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus;


    @Size(max = 100)
    @NotNull
    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @NotNull
    @Column(name = "total_amount", nullable = false)
    private Long total_amount;

    @Column(name = "order_reference", unique = true, nullable = false, updatable = false)
    private String orderReference;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItems> orderItems;
}
