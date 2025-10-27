package com.ecommerce;

import com.ecommerce.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>{
    boolean existsByEventId(String eventId);
}
