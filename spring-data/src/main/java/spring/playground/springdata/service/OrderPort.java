package spring.playground.springdata.service;

import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.entity.order.OrderSearch;

import java.util.List;

public interface OrderPort {
    Long order(Long memberId, Long productId, int count);
    void cancelOrder(Long orderId);
    List<Order> findOrders(OrderSearch orderSearch);
}
