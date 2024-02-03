package spring.playground.springdata.persistence.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
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


    public List<Order> fetchOrders() {
        return jpaQueryFactory
                .selectFrom(order)
                .fetch();
    }

    public Order fetchOneOrder(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetchOne();
    }

    public Order fetchFirstOrder(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetchFirst();
    }
    // 만약 페이징 쿼리가 복잡해지는 경우, 실제 컨텐츠를 가져오는 쿼리와 Count 를 가져오는 쿼리가 다를 수 있음(성능 최적화)
    // 따라서, 복잡하고 성능이 중요한 경우 fetchResults() 를 사용하지 않고 쿼리 두번을 사용하는 것이 좋음
    // 전체 조회수가 필요한 경우 사용하기도 함
    public QueryResults<Order> fetchResults(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetchResults();
    }

    public QueryResults<Order> fetchResultsWithPaging(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .offset(orderSearch.getOffset())
                .limit(orderSearch.getLimit())
                .fetchResults();
    }

    public long fetchCount(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetchCount();
    }

    /**
     * 정렬 관련
     * 내림차순, 오름차순
     * null 여부에 따른 정렬
     */
    public List<Order> fetchOrdersWithSortWithNullLast(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .orderBy(order.status.desc(), order.member.name.asc().nullsLast())
                .fetch();
    }

    public List<Order> fetchOrdersWithSortWithNullFirst(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .orderBy(order.status.desc(), order.member.name.asc().nullsFirst())
                .fetch();
    }


    private static BooleanExpression orderStatus(OrderStatus orderStatus) {
        return order.status.eq(orderStatus);
    }

    private static BooleanExpression nameContains(String memberName) {
        return memberName != null ? order.member.name.eq(memberName) : null;
    }
}
