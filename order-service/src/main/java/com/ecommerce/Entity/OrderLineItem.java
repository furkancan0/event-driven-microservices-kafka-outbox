package com.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "order_line_items")
public class OrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_line_gen")
    @SequenceGenerator(name = "order_line_gen", sequenceName = "order_line_seq", initialValue = 100, allocationSize = 1)
    private Long id;

    @Column(name = "sku_code", nullable = false)
    private String skuCode;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "order_id",referencedColumnName = "id", nullable = false)
    private Order order;

}