package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
public class Inventory extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;


    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Product product;

}
