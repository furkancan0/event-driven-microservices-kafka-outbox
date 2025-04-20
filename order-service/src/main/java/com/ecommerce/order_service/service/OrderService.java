package com.ecommerce.order_service.service;

import com.ecommerce.order_service.Entity.Order;
import com.ecommerce.order_service.Entity.OrderLineItem;
import com.ecommerce.order_service.client.InventoryServiceClient;
import com.ecommerce.order_service.dto.OrderLineItemRequest;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.Status;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService{

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
    private final InventoryServiceClient inventoryServiceClient;

    @Transactional
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(Status.PENDING);
        order.setOrderLineItems(createOrderLineItem(orderRequest.getOrderLineItems(), order));
        List<String> skuCodes = getSkuCodes(orderRequest.getOrderLineItems());

        if (inventoryServiceClient.getProductsBySkuCodes(skuCodes)) {
            orderRepository.save(order);
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber());
            kafkaTemplate.send("orderPlacedTopic", orderPlacedEvent);
            return "Order saved successfully";
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private static List<OrderLineItem> createOrderLineItem(List<OrderLineItemRequest> orderLineItemRequests,Order order) {
        return orderLineItemRequests.stream()
                .map(orderLineItemRequest -> {
                    OrderLineItem orderLineItem = new OrderLineItem();
                    orderLineItem.setSkuCode(orderLineItemRequest.getSkuCode());
                    orderLineItem.setPrice(orderLineItemRequest.getPrice());
                    orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
                    orderLineItem.setOrder(order);
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