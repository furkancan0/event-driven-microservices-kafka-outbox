package com.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {
    private String skuCode;
    private String name;
    private Long quantity;
    private boolean inStock;
}