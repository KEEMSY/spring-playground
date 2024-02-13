package spring.playground.springdata.persistence.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    // QueryProjection 을 사용하여, DTO 를 사용할 수 있다.
    // - 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법
    // - 다만 DTO 가 Querydsl 에 의존하게 되므로, DTO 를 사용하는 계층이 Querydsl 에 의존하게 된다.
    //   - DTO 에 QueryDSL 어노테이션을 유지해야한다는 점, DTO 까지 Q 파일을 생성해야 한다는 단점이 존재한다.
    // - 순서를 맞춰야 함을 주의
    public List<OrderDTOByUsingQueryProjection> fetchOrdersWithQueryProjection(OrderSearch orderSearch) {
        return jpaQueryFactory
                .select(new QOrderDTOByUsingQueryProjection(
                        member.name, order.status.stringValue())
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

    // SQL 함수 사용 가능
    // - SQL 함수를 사용할 때에는, Expressions.stringTemplate() 을 사용하여, 사용할 수 있다.
    // - 기본적으로 제공되는 메서드를 활용하는게 더 유용할 때가 있음에 주의한다.
    public List<String> fetchOrderMemberName(OrderSearch orderSearch) {
        return jpaQueryFactory
                .select(order.member.name)
                .from(order)
                .where(order.member.name
                        .eq(Expressions.stringTemplate("function('upper', {0})", orderSearch.getMemberName())),
                        nameContains(orderSearch.getMemberName())
                )
//                .where(
//                        order.member.name.upper().eq(orderSearch.getMemberName())
//                )
                .fetch();
    }


    /*
    단순한 페이징, fetchResults() 를 사용하여 페이징을 처리할 수 있다.
    - fetchResults() 를 사용하면, 쿼리를 두번 발생함을 잊지말자.
    - fetchResults() 를 사용하면, Order By 는 무의미해 지므로 사용하지 않는다.
      - fetchResults() 메서드를 사용하면, 결과를 가져오는 동안에 페이징 처리를 수행한다. 즉, 결과 세트를 여러 페이지로 분할한다.
        그런데 ORDER BY 절을 사용하면, 데이터베이스는 모든 결과를 먼저 정렬하고 나서야 필요한 일부 결과만 반환한다.
        이는 오히려 성능 저하를 일으킬 수 있다.

따라서, 페이징 처리를 위해 fetchResults()를 사용하는 경우에는 ORDER BY 절의 사용이 무의미해지는 것이 아니라, 오히려 성능을 최적화하는 데 도움이 될 수 있습니다.
    */
    public Page<OrderDTOByUsingQueryProjection> fetchOrdersWithPagingByFetchResults(OrderSearch orderSearch, Pageable pageable) {
        QueryResults<OrderDTOByUsingQueryProjection> results = jpaQueryFactory
                .select(new QOrderDTOByUsingQueryProjection(
                        member.name, order.status.stringValue())
                )
                .from(order)
                .leftJoin(order.member, member)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<OrderDTOByUsingQueryProjection> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    /*
    복잡한 페이징, 데이터 내용과 전체 카운트를 별도로 조회한다.
    - 복잡한 페이징에서 사용한다.
    - 데이터 조회 쿼리와 전체 카운트 쿼리를 분리한다.
      페이징 처리를 할 때, 전체 데이터의 수를 알아야 하는데, 이렇게 전체 카운트 쿼리를 별도로 실행하는 것은 쿼리 최적화 측면에서 중요할 수 있다.
      예를 들어, 데이터 조회 쿼리가 매우 복잡하거나, 많은 양의 데이터를 처리해야 하는 경우, 전체 카운트 쿼리를 별도로 실행하지 않으면
      성능 저하가 발생할 수 있다. 이런 경우에는 데이터 조회 쿼리와 전체 카운트 쿼리를 분리하여 각각 실행하는 것이 좋다.
     */
    public Page<OrderDTOByUsingQueryProjection> fetchOrdersWithPagingByFetchResultsWithComplex(OrderSearch orderSearch, Pageable pageable) {
        List<OrderDTOByUsingQueryProjection> content = jpaQueryFactory
                .select(new QOrderDTOByUsingQueryProjection(
                        member.name, order.status.stringValue())
                )
                .from(order)
                .leftJoin(order.member, member)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(order)
                .from(order)
                .leftJoin(order.member, member)
                .where(
                        orderStatus(orderSearch.getOrderStatus()),
                        nameContains(orderSearch.getMemberName())
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);

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
