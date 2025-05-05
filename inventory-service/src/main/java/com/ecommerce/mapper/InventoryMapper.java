package com.ecommerce.mapper;

import com.ecommerce.dto.InventoryResponse;
import com.ecommerce.model.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryMapper {
    public static List<InventoryResponse> toInventoryResponse(List<Inventory> inventories) {
        return inventories.stream().map(inventory -> InventoryResponse.builder()
                .quantity(inventory.getQuantity())
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity()>0).build()).collect(Collectors.toList());
    }
}
