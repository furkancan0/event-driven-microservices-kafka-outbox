package com.ecommerce.Entity;

import com.ecommerce.dto.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_gen")
    @SequenceGenerator(name = "orders_gen", sequenceName = "orders_seq", initialValue = 100, allocationSize = 1)
    private Long id;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderLineItem> orderLineItems = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Status status;
}