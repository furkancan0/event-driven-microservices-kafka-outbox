package com.ecommerce.dto;

import com.ecommerce.models.OrderItem;

import java.time.LocalDateTime;
import java.util.Set;

public record OrderDto(String orderNumber, String userNam,
                       Set<OrderItemDto> items, Customer customer, Address deliveryAddress,
                       OrderStatus status, LocalDateTime createdAt) {
}
