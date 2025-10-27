package com.ecommerce.models;

import java.time.LocalDateTime;

public record OrderEvent(String eventId, String payload, LocalDateTime createdAt) {
}
