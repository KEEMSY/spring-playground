package com.example.springskeleton.adapter.order.persistence;

import com.example.springskeleton.application.ports.OrderRepository;
import com.example.springskeleton.domain.order.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderPersistenceAdapter implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    public OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order findById(Long orderId) {
        return orderJpaRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll();
    }
}
