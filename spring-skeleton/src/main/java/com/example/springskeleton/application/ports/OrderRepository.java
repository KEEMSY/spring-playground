package com.example.springskeleton.application.ports;

import com.example.springskeleton.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long orderId);

    List<Order> findAll();
}
