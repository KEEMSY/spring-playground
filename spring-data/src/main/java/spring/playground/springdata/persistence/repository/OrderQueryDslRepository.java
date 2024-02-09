package spring.playground.springdata.persistence.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
    // Setter 를 사용하여 값을 가져올 때에는, Projections.bean() 을 사용한다.
    // Entity 내 선언된 필드 명을 동일하게 해야함
    public List<OrderDTO> fetchOrdersWithSetter(OrderSearch orderSearch) {
        return jpaQueryFactory
                .select(Projections.bean(OrderDTO.class, order.status, member.name))
                .from(order)
                .join(order.member, member)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetch();
    }

    // 생성자를 활용하는 방법
    public List<OrderDTOWithConstructor> fetchOrdersWithConstructor(OrderSearch orderSearch) {
        return jpaQueryFactory
                .select(Projections.constructor(OrderDTOWithConstructor.class, member.name, order.status))
                .from(order)
                .join(order.member, member)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetch();
    }


    // Field 를 사용하여 값을 가져올 때에는, Projections.fields() 를 사용한다.
    public List<OrderDTOByUsingField> fetchOrdersWithField(OrderSearch orderSearch) {
        return jpaQueryFactory
                .select(Projections.fields(OrderDTOByUsingField.class,
                        order.status, member.name.as("memberName"))    // as 를 사용하여, 이름을 변경할 수 있다.
                )
                .from(order)
                .join(order.member, member)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetch();
    }


    public List<Order> fetchOrdersWithMemberThetaJoin() {
        return jpaQueryFactory
                .select(order)
                .from(order, member)
                .where(
                        order.status.stringValue().eq(member.name)
                )
                .fetch();
    }

    // DB 와 영속성 켄텍스트의 내용이 달라지는 것에 주의
    // - 영속성 컨텍스트를 무시하고, 바로 DB 에 업데이트를 하기 때문에, 영속성 컨텍스트의 내용이 달라질 수 있다.
    // - 따라서, bulk 연산 뒤에는 영속성 컨텍스트를 맞추기 위해, 해당 작업 이후, flush() 를 사용하여 영속성 컨텍스트를 맞추고, DB 와 맞춘뒤
    //   clear() 를 사용하여 영속성 컨텍스트를 초기화 해주는 것이 좋다.
    public long bulkUpdateOrderStatus(OrderSearch orderSearch) {

        return jpaQueryFactory
                .update(order)
                .set(order.status, orderSearch.getOrderStatus())
                .where(
                        nameContains(orderSearch.getMemberName())
                )
                .execute();
    }

    public long bulkDeleteOrderByOrderStatus(OrderSearch orderSearch) {
        // 연관관계가 있는 OrderItem 을 삭제해야 함
        jpaQueryFactory
                .delete(orderItem)
                .where(
                        orderItem.order.status.eq(orderSearch.getOrderStatus()),
                        orderItem.order.in(
                                JPAExpressions
                                        .select(order)
                                        .from(order)
                                        .where(nameContains(orderSearch.getMemberName()))
                        )
                )
                .execute();


        return jpaQueryFactory
                .delete(order)
                .where(orderStatus(orderSearch.getOrderStatus()))
                .execute();
    }

    // OrderItem 연산이긴하나, 잊지 않기위해.. 작성..!
    public long bulkAddOrderItemPrice(int price) {
        return jpaQueryFactory
                .update(orderItem)
                .set(orderItem.orderPrice, orderItem.orderPrice.add(price))
                .execute();
    }

    public long bulkMultiplyOrderItemPrice(int price) {
        return jpaQueryFactory
                .update(orderItem)
                .set(orderItem.orderPrice, orderItem.orderPrice.multiply(price))
                .execute();
    }

    private static BooleanExpression orderStatus(OrderStatus orderStatus) {
        return orderStatus != null ? order.status.eq(orderStatus) : null;
    }

    private static BooleanExpression nameContains(String memberName) {
        return memberName != null ? order.member.name.eq(memberName) : null;
    }

    // 조합을 통해 동적 쿼리를 만들 수 있다. -> 다른 조건에서, 특정 기간에만 주문 가능한 상품 등 조합을 통해 만들 수 있다.
    // 여러 조건을 조합한 것을 하나의 메서드로 만들고, 여러 다른 쿼리에서도 재활용 할 수 있음 + 쿼리 자체의 가독성이 증진되는 장점
    // 단  null 체크는 주의해서 처리해야 한다.
    private BooleanExpression allEq(OrderSearch orderSearch) {
        return orderStatus(orderSearch.getOrderStatus())
                .and(nameContains(orderSearch.getMemberName()));
    }

    // Case 를 활용하여, 조건에 따른 값을 가져오기 보다는, 값을 가져온 뒤 애플리케이션 레벨에서 처리를 하는 것이 좋다.
}
