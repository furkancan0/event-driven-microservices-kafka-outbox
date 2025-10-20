package com.ecommerce.mapper;

import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.dto.OrderItemDto;
import com.ecommerce.dto.OrderStatus;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OrderMapper {
    public static Order convertToEntity(CreateOrderRequest request) {
        Order newOrder = new Order();
        newOrder.setOrderNumber(UUID.randomUUID().toString());
        newOrder.setStatus(OrderStatus.ORDER_CREATED);
        newOrder.setCustomer(request.customer());
        newOrder.setDeliveryAddress(request.deliveryAddress());

        Set<OrderItem> orderItems = new HashSet<>();
        for (OrderItemDto item : request.items()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setCode(item.code());
            orderItem.setName(item.name());
            orderItem.setPrice(item.price());
            orderItem.setQuantity(item.quantity());
            orderItem.setOrder(newOrder);
            orderItems.add(orderItem);
        }

        newOrder.setItems(orderItems);
        return newOrder;
    }
}
