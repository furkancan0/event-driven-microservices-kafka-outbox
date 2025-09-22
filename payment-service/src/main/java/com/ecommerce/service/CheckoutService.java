package com.ecommerce.service;

import com.ecommerce.events.OrderCreatedEvent;
import com.ecommerce.events.PaymentCompletedEvent;
import com.ecommerce.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "payment-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("💳 Processing payment for order " + event.getOrderId());

        // Mock rule: reject totals > 1000
        boolean success = event.getTotal() <= 1000;

        if (success) {
            kafkaTemplate.send("payment-events",
                    new PaymentCompletedEvent(event.getOrderId(), event.getUserId()));
            System.out.println("✅ Payment completed for order " + event.getOrderId());
        } else {
            kafkaTemplate.send("payment-events",
                    new PaymentFailedEvent(event.getOrderId(), event.getUserId(), "Insufficient funds"));
            System.out.println("❌ Payment failed for order " + event.getOrderId());
        }
    }
}