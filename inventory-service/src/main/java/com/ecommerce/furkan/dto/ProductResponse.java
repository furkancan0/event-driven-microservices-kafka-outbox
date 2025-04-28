package com.ecommerce.furkan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String skuCode;
    private String name;
    private int quantity;
    private boolean inStock;

}