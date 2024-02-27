package com.example.springskeleton.application.ports;

import com.example.springskeleton.domain.order.Order;

import java.util.List;

public interface OrderRepository {
    Order findById(Long orderId);

    List<Order> findAll();
}
