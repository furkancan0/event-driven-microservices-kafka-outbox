package com.ecommerce.service;

import com.ecommerce.dto.OrderLineItemRequest;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.dto.Status;
import com.ecommerce.event.OrderPlacedEvent;
import com.ecommerce.Entity.Order;
import com.ecommerce.Entity.OrderLineItem;
import com.ecommerce.client.InventoryServiceClient;
import com.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService{

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final InventoryServiceClient inventoryServiceClient;

    @Transactional
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(Status.PENDING);
        order.setOrderLineItems(new HashSet<>(createOrderLineItem(orderRequest.getOrderLineItems(), order)));
        orderRepository.save(order);
        return "Order saved successfully " + order.getOrderNumber();
    }

    private static List<OrderLineItem> createOrderLineItem(List<OrderLineItemRequest> orderLineItemRequests, Order order) {
        return orderLineItemRequests.stream()
                .map(orderLineItemRequest -> {
                    OrderLineItem orderLineItem = new OrderLineItem();
                    orderLineItem.setOrder(order);
                    orderLineItem.setSkuCode(orderLineItemRequest.getSkuCode());
                    orderLineItem.setPrice(orderLineItemRequest.getPrice());
                    orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
                    return orderLineItem;
                })
                .collect(Collectors.toList());
    }


    private List<String> getSkuCodes(List<OrderLineItemRequest> orderLineItems) {
        return orderLineItems.stream()
                .map(OrderLineItemRequest::getSkuCode)
                .toList();
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}