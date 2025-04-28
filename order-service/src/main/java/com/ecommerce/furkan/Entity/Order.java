package com.ecommerce.furkan.Entity;

import com.ecommerce.furkan.dto.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderId_generator")
    @SequenceGenerator(name = "orderId_generator_generator", sequenceName = "orderId_generator_seq")
    private Long id;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> orderLineItems = new ArrayList<>();

    private Status status;
}