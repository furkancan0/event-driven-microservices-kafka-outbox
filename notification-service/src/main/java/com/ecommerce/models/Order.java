package com.ecommerce.models;

import java.time.LocalDateTime;
import java.util.Set;

public record Order(String orderNumber, String userName, Set<OrderItem> items,
                    Customer customer, Address deliveryAddress, LocalDateTime createdAt) {
}
