package com.ecommerce.service;

import com.ecommerce.client.PaymentInfo;
import com.ecommerce.client.PaymentResponse;
import com.ecommerce.client.PaymentServiceClient;
import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.dto.CreateOrderResponse;
import com.ecommerce.models.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final PaymentServiceClient paymentServiceClient;
    private final OrderTransactionalService orderTransactionalService;

    public OrderService(PaymentServiceClient paymentServiceClient, OrderTransactionalService orderTransactionalService) {
        this.paymentServiceClient = paymentServiceClient;
        this.orderTransactionalService = orderTransactionalService;
    }

    public CreateOrderResponse createOrder(String userName, CreateOrderRequest request) {
        Order order = orderTransactionalService.createNewOrder(userName, request);

        boolean isPaymentSuccess = pay(order);

        orderTransactionalService.updateOrderStatus(order.getOrderNumber(), isPaymentSuccess);

        return new CreateOrderResponse(order.getStatus());
    }

    private boolean pay(Order order) {
        BigDecimal totalAmount = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentInfo paymentInfo = new PaymentInfo(totalAmount ,order.getId());

        PaymentResponse paymentResponse = paymentServiceClient.checkout(paymentInfo).orElseThrow();
        return paymentResponse.currency().contains("$");
    }

}