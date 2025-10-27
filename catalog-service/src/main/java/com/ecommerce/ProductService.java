package com.ecommerce;

import com.ecommerce.models.OrderEvent;
import com.ecommerce.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaListener(topics = "order-created", groupId = "order-product")
    public void sendSomething(OrderEvent orderEvent) {
        if (productRepository.existsByEventId(orderEvent.eventId())) {
            log.warn("Received duplicate OrderCreatedEvent with eventId: {}", orderEvent.eventId());
            return;
        }

        decreaseProductQuantity(1L, 3);
        log.info("Decreased Quantity for Product Id: {}", 1L);
    }

    public void decreaseProductQuantity(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + productId + " not found"));

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock for product " + productId);
        }

        product.setStock(newStock);
        productRepository.save(product);
    }
}
