package spring.playground.springdata.service;

import spring.playground.springdata.persistence.entity.order.Order;

import java.util.List;

public interface OrderPort {
    Long order(Long memberId, Long productId, int count);
    void cancelOrder(Long orderId);
    List<Order> findOrders();
}
