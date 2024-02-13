package spring.playground.springdata.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.entity.order.OrderSearch;
import spring.playground.springdata.persistence.repository.OrderDTOByUsingQueryProjection;

import java.util.List;

public interface OrderPort {
    Long order(Long memberId, Long productId, int count);
    void cancelOrder(Long orderId);
    List<Order> findOrders(OrderSearch orderSearch);
    Page<OrderDTOByUsingQueryProjection> searchPageSimple(OrderSearch orderSearch, Pageable pageable);
}
