package com.example.springskeleton.adapter.persistence;

import com.example.springskeleton.application.ports.OrderRepository;
import com.example.springskeleton.domain.order.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderPersistenceAdapter implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    public OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll();
    }
}
