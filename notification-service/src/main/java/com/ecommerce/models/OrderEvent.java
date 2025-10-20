package com.ecommerce.models;

import java.time.LocalDateTime;

public record OrderEvent(String eventId, OrderStatus eventType,
                         String payload, LocalDateTime createdAt) {
}
