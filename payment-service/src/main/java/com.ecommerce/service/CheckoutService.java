package com.ecommerce.service;

import com.ecommerce.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {
    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);
    public PaymentResponse charge(Long orderId, Double amount) {
        PaymentResponse response = new PaymentResponse();
        response.setAmount(amount);
        response.setCurrency("$");
        response.setOrderId(orderId);
        response.setDescription("Payment successful");
        log.info("charge payment successful");
        return response;
    }
}