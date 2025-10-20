package com.ecommerce.controller;

import com.ecommerce.dto.PaymentInfo;
import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/charge")
    public ResponseEntity<PaymentResponse> createPaymentIntent(@RequestBody PaymentInfo paymentInfo) {
        PaymentResponse paymentResponse = checkoutService.charge(paymentInfo.getOrderId(),
                paymentInfo.getAmount());
        return ResponseEntity.ok().body(paymentResponse);
    }
}
