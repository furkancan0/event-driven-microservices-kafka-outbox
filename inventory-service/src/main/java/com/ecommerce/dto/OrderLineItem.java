package com.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderLineItem {
    private Long id;

    private String skuCode;

    private BigDecimal price;

    private Long quantity;
}
