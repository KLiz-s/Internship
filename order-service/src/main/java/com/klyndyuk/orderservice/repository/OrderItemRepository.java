package com.klyndyuk.orderservice.repository;

import com.klyndyuk.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
