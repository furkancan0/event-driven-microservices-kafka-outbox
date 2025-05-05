package com.ecommerce.service;


import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.event.OrderPlacedEvent;
import com.ecommerce.model.Inventory;
import com.ecommerce.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public List<InventoryResponse> getInventoryBySkuCodes(List<String> skuCodes) {
        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);

        return inventories.stream()
                .map(inventory -> new InventoryResponse(
                        inventory.getSkuCode(),
                        inventory.getQuantity() > 0,
                        inventory.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = "orderPlacedTopic", groupId = "inventory1")
    public void sendSomething(OrderPlacedEvent orderPlacedEvent) {
        log.info("Received order event: {}", orderPlacedEvent);

        System.out.println("hey dostum");

        log.info("Inventory updated for event: {}", orderPlacedEvent.getOrderNumber());
    }
}
