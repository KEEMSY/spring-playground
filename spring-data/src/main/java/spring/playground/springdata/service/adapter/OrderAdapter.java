package spring.playground.springdata.service.adapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spring.playground.springdata.persistence.entity.item.Item;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.entity.order.*;
import spring.playground.springdata.persistence.repository.*;
import spring.playground.springdata.service.OrderPort;

import java.util.List;

@Component
public class OrderAdapter implements OrderPort {
    private final MemberJpaRepository memberJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderQueryDslRepository orderQueryDslRepository;
    private final ItemJpaRepository itemJpaRepository;

    public OrderAdapter(MemberJpaRepository memberJpaRepository,
                        OrderJpaRepository orderJpaRepository,
                        OrderQueryDslRepository orderQueryDslRepository,
                        ItemJpaRepository itemJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
        this.orderJpaRepository = orderJpaRepository;
        this.orderQueryDslRepository = orderQueryDslRepository;
        this.itemJpaRepository = itemJpaRepository;
    }

    @Override
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberJpaRepository.findById(memberId).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));
        Item item = itemJpaRepository.findById(itemId).orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderJpaRepository.save(order);

        return order.getId();

    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderJpaRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("존재하지 않는 주문입니다."));
        order.cancel();
    }

    @Override
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderQueryDslRepository.findOrdersWith(orderSearch);
    }

    @Override
    public Page<OrderDTOByUsingQueryProjection> searchPageSimple(OrderSearch orderSearch, Pageable pageable) {
        return orderQueryDslRepository.fetchOrdersWithPagingByFetchResults(orderSearch, pageable);
    }
}
