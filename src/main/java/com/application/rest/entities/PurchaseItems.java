package com.application.rest.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="items")
public class PurchaseItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="cantidad")
    private Integer cant;

    @ManyToOne()
    @JoinColumn(name="producto_id",nullable = false)
    private Product product;

    @ManyToOne()
    @JoinColumn(name="compra_id",nullable = false)
    private Purchase purchase;

    @Column(name="precio")
    private BigDecimal price;
}
