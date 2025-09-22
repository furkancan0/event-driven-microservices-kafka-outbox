package com.ecommerce.service;

import com.common.dto.OrderRequestDto;
import com.common.event.OrderEvent;
import com.common.event.OrderStatus;
import com.ecommerce.entity.Order;
import com.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService{
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Transactional
    public String placeOrder(OrderRequestDto orderRequestDto) {
        Order order = convertDtoToEntity(orderRequestDto);
        orderRepository.save(order);

        OrderEvent orderEvent = new OrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);

        kafkaTemplate.send("ORDER_CREATED", orderEvent);
        return "ORDER CREATED SUCCESSFULLY";
    }

    private Order convertDtoToEntity(OrderRequestDto dto) {
        Order order = new Order();
        order.setProductId(dto.getProductId());
        order.setUserId(dto.getUserId());
        order.setStatus(OrderStatus.ORDER_CREATED);
        order.setPrice(dto.getAmount());
        return order;
    }
}