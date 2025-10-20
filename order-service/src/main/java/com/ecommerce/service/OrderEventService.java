package com.ecommerce.service;

import com.ecommerce.dto.OrderStatus;
import com.ecommerce.models.OrderEvent;
import com.ecommerce.repository.OrderEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderEventService {
    private static final Logger log = LoggerFactory.getLogger(OrderEventService.class);

    private final OrderEventRepository orderEventRepository;
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventService(OrderEventRepository orderEventRepository, ObjectMapper objectMapper,
                             KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.orderEventRepository = orderEventRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    void save(OrderEvent event) {
        this.orderEventRepository.save(event);
    }

    //works every 5 sec
    public void publishOrderEvents() {
        Sort sort = Sort.by("createdAt").ascending();
        List<OrderEvent> events = orderEventRepository.findTop10ByIsDelivered(sort, false);
        log.info("Found {} Order Events to be published", events.size());
        for (OrderEvent event : events) {
            publishEvent(event);
            event.setDelivered(true);
            orderEventRepository.save(event);
        }
    }

    private void publishEvent(OrderEvent event) {
        OrderStatus eventType = event.getEventType();
        switch (eventType) {
            case IN_PROCESS:
                kafkaTemplate.send("order-created", event);
                log.info("Order {} Events published to kafka", event.getEventId());
                break;
            default:
                log.warn("Unsupported OrderEventType: {}", eventType);
        }
    }
    private String toJsonPayload(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJsonPayload(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
