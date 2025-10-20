package com.ecommerce.service;

import com.ecommerce.client.PaymentInfo;
import com.ecommerce.client.PaymentResponse;
import com.ecommerce.client.PaymentServiceClient;
import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.dto.CreateOrderResponse;
import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderStatus;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderEvent;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderEventService orderEventService;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, PaymentServiceClient paymentServiceClient,
                        OrderEventService orderEventService, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.orderEventService = orderEventService;
        this.objectMapper = objectMapper;
    }

    public CreateOrderResponse createOrder(String userName, CreateOrderRequest request) {
        Order order = createNewOrder(userName, request);

        boolean isPaymentSuccess = pay(order);

        updateOrderStatus(order.getOrderNumber(), isPaymentSuccess);

        return new CreateOrderResponse(order.getStatus());
    }

    @Transactional
    protected Order createNewOrder(String userName, CreateOrderRequest request) {
        Order newOrder = OrderMapper.convertToEntity(request);
        newOrder.setUserName(userName);
        Order savedOrder = this.orderRepository.save(newOrder);
        log.info("Created Order with orderNumber={}", savedOrder.getOrderNumber());

        saveEvent(OrderStatus.ORDER_CREATED, savedOrder.getOrderNumber());

        return savedOrder;
    }

    @Transactional
    protected void updateOrderStatus(String orderNumber, boolean isPaymentSuccess) {
        Order order = this.orderRepository.findByOrderNumber(orderNumber).orElseThrow();

        if (isPaymentSuccess) {
            log.info("OrderNumber: {} purchased successfully", order.getOrderNumber());
            order.setStatus(OrderStatus.IN_PROCESS);
            saveEvent(OrderStatus.IN_PROCESS, orderNumber);
        }else{
            log.info("OrderNumber: {} can not be delivered", order.getOrderNumber());
            order.setStatus(OrderStatus.CANCELLED);
            saveEvent(OrderStatus.CANCELLED, orderNumber);
        }
        this.orderRepository.save(order);
    }

    @Transactional
    protected void saveEvent(OrderStatus orderStatus, String orderNumber) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderNumber(orderNumber);
        orderEvent.setEventType(orderStatus);
        orderEvent.setEventId(UUID.randomUUID().toString());
        orderEvent.setPayload("{\"orderNumber\": " + orderNumber + "}");

        // Transactional Outbox
        orderEventService.save(orderEvent);
    }

    private boolean pay(Order order) {
        BigDecimal totalAmount = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentInfo paymentInfo = new PaymentInfo(totalAmount ,order.getId());

        PaymentResponse paymentResponse = paymentServiceClient.checkout(paymentInfo).orElseThrow();
        return paymentResponse.currency().contains("$");
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