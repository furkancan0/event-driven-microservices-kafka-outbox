package com.ecommerce.client;

public record PaymentResponse(String currency, Double amount, String description, Long orderId) {
}
