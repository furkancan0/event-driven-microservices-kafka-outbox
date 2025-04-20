package com.ecommerce.inventory_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "sku_code", nullable = false)
    private String skuCode;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
