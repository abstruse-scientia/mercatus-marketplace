package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventory_item", schema = "mercatus_db", uniqueConstraints = {
        @UniqueConstraint(name = "sku", columnNames = {"sku"})
})
public class InventoryItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @NotNull
    @Column(name = "total_stock", nullable = false)
    private Integer totalStock;

    @NotNull
    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock = 0;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Size(max = 255)
    @NotNull
    @Column(name = "order_reference", nullable = false, length = 255)
    private String orderReference;

}