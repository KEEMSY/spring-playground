package spring.playground.springdata.service.adapter;

import org.springframework.stereotype.Component;
import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.repository.ItemJpaRepository;
import spring.playground.springdata.persistence.repository.MemberJpaRepository;
import spring.playground.springdata.persistence.repository.OrderJpaRepository;
import spring.playground.springdata.service.OrderPort;

import java.util.List;

@Component
public class OrderAdapter implements OrderPort {
    private final MemberJpaRepository memberJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final ItemJpaRepository itemJpaRepository;

    public OrderAdapter(MemberJpaRepository memberJpaRepository,
                        OrderJpaRepository orderJpaRepository,
                        ItemJpaRepository itemJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
        this.orderJpaRepository = orderJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
    }

    @Override
    public Long order(Long memberId, Long productId, int count) {
        return null;
    }

    @Override
    public void cancelOrder(Long orderId) {

    }

    @Override
    public List<Order> findOrders() {
        return null;
    }
}
