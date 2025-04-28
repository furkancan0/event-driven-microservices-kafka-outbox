package com.ecommerce.furkan.controller;

import com.ecommerce.furkan.dto.InventoryResponse;
import com.ecommerce.furkan.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/by-skus")
    public ResponseEntity<List<InventoryResponse>> getInventoryBySkuCodes(@RequestBody List<String> skuCodes) {
        List<InventoryResponse> response = inventoryService.getInventoryBySkuCodes(skuCodes);
        return ResponseEntity.ok(response);
    }
}