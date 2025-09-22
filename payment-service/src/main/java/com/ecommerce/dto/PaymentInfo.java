package com.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentInfo {
    private BigDecimal amount;
    private String currency;
    private String paymentMethodType;
}
