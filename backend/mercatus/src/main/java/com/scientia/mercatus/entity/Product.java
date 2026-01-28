package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="product_id", nullable = false)
    private Long productId;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="description")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "popularity", nullable = false)
    private Long popularity = 0L;

    @Column(name = "total_sold_quantity", nullable = false)
    private Long totalSoldQuantity = 0L;

    @JoinColumn(name = "category_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;


    @Size(max = 100)
    @NotNull
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull

    @Column(name = "is_sellable", nullable = false)
    private Boolean isSellable = true;

}