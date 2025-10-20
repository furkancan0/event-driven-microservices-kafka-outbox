package com.ecommerce.client;


import java.math.BigDecimal;

public record PaymentInfo(BigDecimal amount, Long orderId) { }
