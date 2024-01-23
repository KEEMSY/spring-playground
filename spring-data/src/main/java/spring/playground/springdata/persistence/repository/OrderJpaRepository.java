package spring.playground.springdata.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.playground.springdata.persistence.entity.order.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
