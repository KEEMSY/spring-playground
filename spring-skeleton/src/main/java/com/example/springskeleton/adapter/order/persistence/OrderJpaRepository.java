package com.example.springskeleton.adapter.order.persistence;

import com.example.springskeleton.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
