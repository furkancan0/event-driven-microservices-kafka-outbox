package com.ecommerce.service;

import com.ecommerce.models.Notification;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderEvent;
import com.ecommerce.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationRepository notificationRepository, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-created", groupId = "order-notification")
    public void sendSomething(OrderEvent orderEvent) {
        if (notificationRepository.existsByEventId(orderEvent.eventId())) {
            log.warn("Received duplicate OrderCreatedEvent with eventId: {}", orderEvent.eventId());
            return;
        }

        Order order = fromJsonPayload(orderEvent.payload(), Order.class);

        log.info("Received a OrderCreatedEvent with orderNumber:{}: ", order.orderNumber());
        Notification notification = new Notification();
        notification.setEventId(orderEvent.eventId());
        notification.setOrderNumber(order.orderNumber());
        notification.setCreatedAt(order.createdAt());
        notificationRepository.save(notification);
    }

    private <T> T fromJsonPayload(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
