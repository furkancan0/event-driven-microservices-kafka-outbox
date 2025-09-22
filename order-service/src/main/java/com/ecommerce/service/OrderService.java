package com.ecommerce.service;

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
}