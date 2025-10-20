package com.ecommerce.repository;

import com.ecommerce.models.OrderEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
    List<OrderEvent> findTop10ByIsDelivered(Sort sort, boolean isDelivered);
}