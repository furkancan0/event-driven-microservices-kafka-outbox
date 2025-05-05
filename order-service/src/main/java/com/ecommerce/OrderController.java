package com.ecommerce;

import com.ecommerce.Entity.Order;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("")
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Order> getProductById(@PathVariable Long productId) {
        return new ResponseEntity<>(orderService.getOrder(productId), HttpStatus.OK);
    }
}