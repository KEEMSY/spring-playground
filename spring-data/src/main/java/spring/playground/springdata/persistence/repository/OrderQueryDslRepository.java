package spring.playground.springdata.persistence.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.entity.order.OrderSearch;
import spring.playground.springdata.persistence.entity.order.OrderStatus;

import java.util.List;

import static spring.playground.springdata.persistence.entity.order.QOrder.order;

@Repository
public class OrderQueryDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public OrderQueryDslRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<Order> findOrdersWith(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetch();
    }

    private static BooleanExpression orderStatus(OrderStatus orderStatus) {
        return order.status.eq(orderStatus);
    }

    private static BooleanExpression nameContains(String memberName) {
        return memberName != null ? order.member.name.eq(memberName): null;
    }
}