package spring.playground.springdata.persistence.repository;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.playground.springdata.DatabaseCleanup;
import spring.playground.springdata.persistence.OrderStep;
import spring.playground.springdata.persistence.entity.common.Category;
import spring.playground.springdata.persistence.entity.item.Album;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.entity.member.QMember;
import spring.playground.springdata.persistence.entity.order.*;
import spring.playground.springdata.persistence.entity.order.Order;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
class OrderQueryDslRepositoryTest {
    private final OrderQueryDslRepository orderQueryDslRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final OrderStep orderStep;
    private final DatabaseCleanup databaseCleanup;
    @PersistenceContext
    private EntityManager em;
    private int orderItemPrice;
    private int orderCount;

    @Autowired
    OrderQueryDslRepositoryTest(OrderQueryDslRepository orderQueryDslRepository,
                                ItemJpaRepository itemJpaRepository,
                                OrderStep orderStep, DatabaseCleanup databaseCleanup) {
        this.orderQueryDslRepository = orderQueryDslRepository;
        this.itemJpaRepository = itemJpaRepository;
        this.orderStep = orderStep;
        this.databaseCleanup = databaseCleanup;
    }

    @BeforeEach
    void setUp() {
        orderCount = 10;
        for (int i = 0; i < orderCount; i++) {
            Member member = orderStep.saveMember("test" + i);
            orderItemPrice = 1000;
            Album album = orderStep.saveAlbum("album" + i, orderItemPrice, 10);
            Category category = orderStep.addCategory(album);
            album.setCategories(new ArrayList<>());
            album.getCategories().add(category);
            itemJpaRepository.save(album);
            orderStep.createOrder(member, album.getId());
        }
    }

    @AfterEach
    void tearDown() {
        databaseCleanup.execute();
    }

    @Test
    @DisplayName("fetch() 는 리스트를 조회하며, 데이터가 없으면 빈 리스트를 반환한다.")
    void fetchOrders() {
        // given
        // befoerEach 에서 동작

        // when
        var orders = orderQueryDslRepository.fetchOrders();

        // then
        assertEquals(10, orders.size());
    }

    @Test
    @DisplayName("fetchOne() 은 단일 데이터를 조회한다.")
    @Transactional
    void fetchOneOrder() {
        // given
        // befoerEach 에서 동작
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        Order order = orderQueryDslRepository.fetchOneOrder(orderSearch);

        // then
        assertEquals("test0", order.getMember().getName());
    }

    @Test
    @DisplayName("fetchOne() 은 데이터가 없으면 null 을 반환한다.")
    void fetchOneOrderWithError() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        String unknownMemberName = "unknownMemberName";
        orderSearch.setMemberName(unknownMemberName);

        // when
        Order order = orderQueryDslRepository.fetchOneOrder(orderSearch);

        // then
        assertEquals(null, order);
    }

    @Test
    @DisplayName("fetchOne() 은 데이터가 여러개일 경우 에러를 반환한다.")
    void fetchOneOrderWithMultipleData() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when, then
        assertThrows(NonUniqueResultException.class, () -> orderQueryDslRepository.fetchOneOrder(orderSearch));
    }

    @Test
    @DisplayName("fetchFirst() 는 데이터가 여러개일 경우 첫번째 데이터를 반환한다.")
    @Transactional
    void fetchFirstOrderWithMultipleData() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        Order order = orderQueryDslRepository.fetchFirstOrder(orderSearch);

        // then
        assertEquals("test0", order.getMember().getName());
    }

    @Test
    @DisplayName("fetchFirst() 는 데이터가 없으면 null 을 반환한다.")
    void fetchFirstOrderWithError() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        String unknownMemberName = "unknownMemberName";
        orderSearch.setMemberName(unknownMemberName);

        // when
        Order order = orderQueryDslRepository.fetchFirstOrder(orderSearch);

        // then
        assertEquals(null, order);
    }

    @Test
    @DisplayName("fetchResults() 는 페이징 처리된 결과를 반환한다.")
    void fetchResults() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        var orders = orderQueryDslRepository.fetchResults(orderSearch);
        List<Order> content = orders.getResults();

        // then
        assertEquals(1, orders.getTotal());
        assertEquals(1, content.size());
    }

    @Test
    @DisplayName("fetchResults() 는 데이터가 없으면 빈 결과를 반환한다.")
    void fetchResultsWithError() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        String unknownMemberName = "unknownMemberName";
        orderSearch.setMemberName(unknownMemberName);

        // when
        var orders = orderQueryDslRepository.fetchResults(orderSearch);
        List<Order> content = orders.getResults();

        // then
        assertEquals(0, orders.getTotal());
        assertEquals(0, content.size());
    }

    @Test
    @DisplayName("fetchCount() 는 데이터의 개수를 반환한다.")
    void fetchCount() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        long count = orderQueryDslRepository.fetchCount(orderSearch);

        // then
        assertEquals(1, count);
    }

    @Test
    @DisplayName("fetchCount() 는 데이터가 없으면 0을 반환한다.")
    void fetchCountWithError() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        String unknownMemberName = "unknownMemberName";
        orderSearch.setMemberName(unknownMemberName);

        // when
        long count = orderQueryDslRepository.fetchCount(orderSearch);

        // then
        assertEquals(0, count);
    }

    /**
     * 주문 정렬 순서
     * 1. 주문 상태 내림차순(descending)
     * 2. 회원 이름 오름차순(asceding)
     * 단, 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    @DisplayName("fetchOrdersWithSort() 는 주문 상태 내림차순, 회원 이름 오름차순으로 정렬된 결과를 반환한다.")
    @Transactional
    void fetchOrdersWithSort() {
        // given
        Member member = orderStep.saveMember(null);
        Album album = orderStep.saveAlbum("album for null member" , 1000, 10);
        Category category = orderStep.addCategory(album);
        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);
        itemJpaRepository.save(album);
        orderStep.createOrder(member, album.getId());

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        List<Order> orders = orderQueryDslRepository.fetchOrdersWithSortWithNullLast(orderSearch);

        // then
        assertEquals(11, orders.size());
        assertEquals("test0", orders.get(0).getMember().getName());
    }

    @Test
    @DisplayName("fetchOrdersWithSort() 는 회원 이름이 없으면 마지막에 출력한다.")
    @Transactional
    void fetchOrdersWithSortWithNull() {
        // given
        Member member = orderStep.saveMember(null);
        Album album = orderStep.saveAlbum("album for null member" , 1000, 10);
        Category category = orderStep.addCategory(album);
        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);
        itemJpaRepository.save(album);
        orderStep.createOrder(member, album.getId());

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        List<Order> orders = orderQueryDslRepository.fetchOrdersWithSortWithNullLast(orderSearch);
        int count = orders.size();

        // then
        assertEquals(11, orders.size());
        assertEquals(null, orders.get(count-1).getMember().getName());
    }

    @Test
    @DisplayName("fetchOrdersWithSort() 는 회원 이름이 없으면 처음에 출력한다.")
    @Transactional
    void fetchOrdersWithSortWithNullFirst() {
        // given
        Member member = orderStep.saveMember(null);
        Album album = orderStep.saveAlbum("album for null member" , 1000, 10);
        Category category = orderStep.addCategory(album);
        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);
        itemJpaRepository.save(album);
        orderStep.createOrder(member, album.getId());

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        List<Order> orders = orderQueryDslRepository.fetchOrdersWithSortWithNullFirst(orderSearch);

        // then
        assertEquals(11, orders.size());
        assertEquals(null, orders.get(0).getMember().getName());
    }

    @Test
    @DisplayName("페이징 쿼리 테스트")
    void fetchOrdersWithPaging() {
        // given
        int limit = 5;
        int offset = 0;
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setLimit(limit);
        orderSearch.setOffset(offset);

        // when
        QueryResults<Order> orders = orderQueryDslRepository.fetchResultsWithPaging(orderSearch);
        List<Order> content = orders.getResults();

        // then
        assertEquals(10, orders.getTotal());
        assertEquals(5, content.size());
        assertEquals(content.size(), limit);
    }

    @Test
    @DisplayName("Groyup by 테스트: 고객명 별 평균 주문 아이템 수")
    @Transactional
    void fetchOrdersGroupByMemberName() {
        // given
        String memberName = "test0";
        Member member = orderStep.saveMember(memberName);
        int newOrderItemPrice = 2000;
        Album album = orderStep.saveAlbum("album for test0 member" , newOrderItemPrice, 10);
        Category category = orderStep.addCategory(album);
        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);
        itemJpaRepository.save(album);
        orderStep.createOrder(member, album.getId());

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        List<Tuple> orders = orderQueryDslRepository.fetchOrdersWithGroupBy(orderSearch);

        // then
        Tuple orderTuple = orders.get(0);
        String targetCustomerName = orderTuple.get(QMember.member.name);
        Double averageOrderAmount = orderTuple.get(QOrderItem.orderItem.orderPrice.avg());


        int averageOrderItemPrice = (orderItemPrice + newOrderItemPrice) / 2;

        assertEquals(orders.size(), 10);
        assertEquals(targetCustomerName, memberName);
        assertEquals(averageOrderItemPrice, averageOrderAmount.intValue());
    }

    @PersistenceUnit
    EntityManagerFactory emf;


    @Test
    @DisplayName("Fetch Join 미적용 확인")
    void fetchOrdersWithoutFetchJoin() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        Order orders = orderQueryDslRepository.fetchOneOrder(orderSearch);

        // then
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(orders.getOrderItems());
        assertEquals(false, loaded);
    }

    @Test
    @DisplayName("Fetch Join 적용 확인")
    void fetchOrdersWithFetchJoin() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        Order orders = orderQueryDslRepository.fetchOneOrderWithFetchJoin(orderSearch);

        // then
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(orders.getOrderItems());
        assertEquals(true, loaded);
    }

    @Test
    @DisplayName("DTO 조회 테스트: Setter 를 활용하는 방법")
    @Transactional
    void fetchOrdersWithSetter() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        List<OrderDTO> orders = orderQueryDslRepository.fetchOrdersWithSetter(orderSearch);

        // then
        System.out.println(orders);
        System.out.println(orders.get(0).getName());
        assertEquals(1, orders.size());
        assertEquals("test0", orders.get(0).getName());
    }

    @Test
    @DisplayName("DTO 조회 테스트: Field 를 활용하는 방법")
    void fetchOrdersWithField() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        List<OrderDTOByUsingField> orders = orderQueryDslRepository.fetchOrdersWithField(orderSearch);

        // then
        assertEquals(1, orders.size());
        assertEquals("test0", orders.get(0).getMemberName());
    }

    @Test
    @DisplayName("DTO 조회 테스트: 생성자를 활용하는 방법")
    void fetchOrdersWithConstructor() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test0");

        // when
        List<OrderDTOWithConstructor> orders = orderQueryDslRepository.fetchOrdersWithConstructor(orderSearch);

        // then
        assertEquals(1, orders.size());
        assertEquals("test0", orders.get(0).getMemberName());
    }

    @Test
    @DisplayName("Bulk bulkUpdateOrderStatus 테스트")
    @Transactional
    void bulkUpdate() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.CANCEL);
        orderSearch.setMemberName("test0");

        // when
        long count = orderQueryDslRepository.bulkUpdateOrderStatus(orderSearch);

        // then
        orderSearch = new OrderSearch();
        orderSearch.setMemberName("test0");

        em.flush();
        em.clear();
        
        Order order = orderQueryDslRepository.fetchOneOrder(orderSearch);


        assertEquals(OrderStatus.CANCEL, order.getStatus());
    }

    @Test
    @DisplayName("Bulk bulkDeleteOrderStatus 테스트")
    @Transactional
    void bulkDelete() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.CANCEL);
        orderSearch.setMemberName("test0");

        long count = orderQueryDslRepository.bulkUpdateOrderStatus(orderSearch);

        em.flush();
        em.clear();

        // when
        orderQueryDslRepository.bulkDeleteOrderByOrderStatus(orderSearch);

        // then
        orderSearch = new OrderSearch();
        orderSearch.setMemberName("test0");

        em.flush();
        em.clear();

        List<Order> orders = orderQueryDslRepository.fetchOrders();

        assertEquals(9, orders.size());
    }

    @Test
    @DisplayName("SQL Function 테스트: upper() 함수를 사용하여 소문자를 대문자로 변경하여 조회")
    void fetchOrdersWithSQLFunction() {
        // given
        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("TEST0");

        // when
        List<String> upperOrderMemberNames = orderQueryDslRepository.fetchOrderMemberName(orderSearch);

        // then
        assertEquals(1, upperOrderMemberNames.size());
        assertEquals("test0", upperOrderMemberNames.get(0));
    }

    @Test
    @DisplayName(("theta join 테스트"))
    @Disabled
//    @Transactional
    void fetchOrdersWithThetaJoin() {
        // given
        String memberName = OrderStatus.ORDER.toString();
        Member member = orderStep.saveMember(memberName);
        int newOrderItemPrice = 2000;
        Album album = orderStep.saveAlbum("ORDER" , newOrderItemPrice, 10);
        Category category = orderStep.addCategory(album);
        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);
        itemJpaRepository.save(album);
        orderStep.createOrder(member, album.getId());

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        List<Order> orders = orderQueryDslRepository.fetchOrdersWithMemberThetaJoin();

        // then
        for (Order order : orders) {
            System.out.println(order.getMember().getName());
            System.out.println(order.getStatus());

        }
        assertEquals(1, orders.size());
        assertEquals(memberName, orders.get(0).getMember().getName());
    }
}