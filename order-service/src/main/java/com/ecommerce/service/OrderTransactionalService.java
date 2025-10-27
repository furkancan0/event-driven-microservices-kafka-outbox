package com.ecommerce.service;

import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.dto.OrderStatus;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderEvent;
import com.ecommerce.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class OrderTransactionalService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventService orderEventService;
    private final ObjectMapper objectMapper;

    public OrderTransactionalService(OrderRepository orderRepository, OrderEventService orderEventService, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderEventService = orderEventService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order createNewOrder(String userName, CreateOrderRequest request) {
        Order newOrder = OrderMapper.convertToEntity(request);
        newOrder.setUserName(userName);
        Order savedOrder = this.orderRepository.save(newOrder);
        log.info("Created Order with orderNumber={}", savedOrder.getOrderNumber());

        saveEvent(OrderStatus.ORDER_CREATED, savedOrder.getOrderNumber());

        return savedOrder;
    }

    @Transactional
    public void updateOrderStatus(String orderNumber, boolean isPaymentSuccess) {
        Order order = this.orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (isPaymentSuccess) {
            log.info("OrderNumber: {} purchased successfully", order.getOrderNumber());
            order.setStatus(OrderStatus.IN_PROCESS);
            saveEvent(OrderStatus.IN_PROCESS, orderNumber);
        } else {
            log.info("OrderNumber: {} can not be delivered", order.getOrderNumber());
            order.setStatus(OrderStatus.CANCELLED);
            saveEvent(OrderStatus.CANCELLED, orderNumber);
        }
        this.orderRepository.save(order);
    }

    @Transactional
    public void saveEvent(OrderStatus orderStatus, String orderNumber) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderNumber(orderNumber);
        orderEvent.setEventType(orderStatus);
        orderEvent.setEventId(UUID.randomUUID().toString());
        orderEvent.setPayload("{\"orderNumber\": " + orderNumber + "}");

        // Transactional Outbox
        orderEventService.save(orderEvent);
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