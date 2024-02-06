package spring.playground.springdata.persistence.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.entity.order.OrderSearch;
import spring.playground.springdata.persistence.entity.order.OrderStatus;

import java.util.List;

import static spring.playground.springdata.persistence.entity.member.QMember.member;
import static spring.playground.springdata.persistence.entity.order.QOrder.order;
import static spring.playground.springdata.persistence.entity.order.QOrderItem.orderItem;

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
    // 이유: 자동화된 count 쿼리는 원본 쿼리와 같이 모두 조인을 해버리기 때문에 성능이 떨어질 수 있음
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

    /**
     * 집계 함수
     * count, sum, avg, max, min
     * groupBy
     * having
     *
     * data 타입(selcet 필회)이 여러개로 들어올 때는 Tuple 을 사용한다.
     *   - Tuple 은 Querydsl 의 특징이자 Querydsl 에 종속됨을 표현한다.
     *   - Repository 에서 Tuple 을 조회한 뒤 다른 계층으로 전달할 때에 DTO 로 변환하여 사용하는 것이 좋다.
     *   - Tuple 은 여러 타입을 담을 수 있는 컨테이너이다.
     *   - Tuple 은 여러 타입을 담을 수 있지만, 실무에서는 잘 사용하지 않으며, Dto 를 사용한다.
     *   - Tuple.get() 을 사용하여 값을 꺼내올 수 있다.
     *   - Tuple.get(0) 은 첫번째 값, Tuple.get(1) 은 두번째 값이다.
     */

    // 고객의 이름애 따른 평균 주문 금액 조회
    public List<Tuple> fetchOrdersWithGroupBy(OrderSearch orderSearch) {
        return jpaQueryFactory
                .select(member.name, orderItem.orderPrice.avg())
                .from(order)
                .join(order.member, member)
                .join(order.orderItems, orderItem)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .groupBy(member.name)
                .orderBy(member.name.asc())
                .fetch();
    }

    /**
     *   join(조인 대상, 별칭으로 사용할 Q타입)
     *   - join(), innerJoin(): 내부 조인(inner jpin)
     *   - leftJoin(): 왼쪽 외부 조인(left outer join)
     *   - rightJoin(): 오른쪽 외부 조인(right outer join)
     *   - on(): 조인 대상 필터링
     *   - fetchJoin(): 연관된 엔티티를 SQL 한번에 조회(성능 최적화
     */
    public Order fetchOneOrderWithFetchJoin(OrderSearch orderSearch) {
        return jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.orderItems, orderItem).fetchJoin()
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetchOne();
    }

    public List<OrderDTOByUsingSetter> fetchOrdersWithSetter(OrderSearch orderSearch) {
        return jpaQueryFactory
                .select(Projections.bean(OrderDTOByUsingSetter.class, order.status, member.name))
                .from(order)
                .join(order.member, member)
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
        return memberName != null ? order.member.name.eq(memberName) : null;
    }

    // Case 를 활용하여, 조건에 따른 값을 가져오기 보다는, 값을 가져온 뒤 애플리케이션 레벨에서 처리를 하는 것이 좋다.
}
