package spring.playground.springdata.persistence.entity.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.playground.springdata.persistence.entity.member.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

/**
 * LAZY 를  EAGAR 로 하는 것이 답이 아닌 이유
 * JPA 메서드(ex. findAllByString() 은 JQPL 이 날라간다.
 * 즉 Order 만 가져오나, EAGER 인 필드 값을 보고 일일히 쿼리를 다 날림(단건으로 조회 함, N+1 문제 발생)
 * 뿐만아니라 해당 API 를 사용하는 곳에서 EAGER 가 필요없는 경우(값을 사용하지 않는 경우)에도 해당 쿼리를 발생시킴 이로인해 성능최적화의 여지를 없애게 됨
 * 따라서 항상 지연로디응ㄹ 기본으로하고, 성능 최적화가 필요한 경우에는 Fetch Join 을 사용한다.(V3참고)
 */

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // default_batch_fetch_size(글로벌한 사용) 이 아닌 세부 사항에 적용할 경우, @BatchSize()(컬렉션인 경우) 어느테이션을 필드에 사용한다.
    // 1000개씩 In 할 경우: @BatchSize(size = 1000)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // OrderItem ... : orderItem 을 여러개 넣을 수 있음. 넣어지는 것은 배열로 들어감
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}

