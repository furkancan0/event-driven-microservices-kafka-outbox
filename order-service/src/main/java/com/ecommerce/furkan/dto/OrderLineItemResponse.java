package com.ecommerce.furkan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemResponse {
    private String skuCode;
    private Long quantity;
}
