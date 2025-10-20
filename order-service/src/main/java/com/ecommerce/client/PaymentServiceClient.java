package com.ecommerce.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class PaymentServiceClient {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceClient.class);

    private final RestClient restClient;

    PaymentServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @CircuitBreaker(name = "payment-service")
    @Retry(name = "payment-service", fallbackMethod = "processCheckoutFallback")
    public Optional<PaymentResponse> checkout(PaymentInfo paymentInfo) {
        log.info("Payment started for order: {}", paymentInfo.orderId());
        var response =
                restClient.post().uri("/api/checkout/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Content-Type", "application/json")
                        .body(paymentInfo)
                        .retrieve()
                        .body(PaymentResponse.class);
        return Optional.ofNullable(response);
    }

    Optional<PaymentResponse> processCheckoutFallback(PaymentInfo paymentInfo, Throwable t) {
        log.info("payment-service charge fallback: code:{}, Error: {} ", paymentInfo.orderId(), t.getMessage());
        return Optional.empty();
    }
}
