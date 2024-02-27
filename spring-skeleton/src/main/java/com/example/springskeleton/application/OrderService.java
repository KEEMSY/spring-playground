package com.example.springskeleton.application;

import com.example.springskeleton.application.ports.OrderRepository;
import com.example.springskeleton.domain.order.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public Order order() {
        return null;
    }

    public Order getOrderById(final Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }
}
