package com.ecommerce;

import com.common.dto.OrderRequestDto;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("create")
    public String placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return orderService.placeOrder(orderRequestDto);
    }
}