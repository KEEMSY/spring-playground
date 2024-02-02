package spring.playground.springdata.persistence.repository;

import com.querydsl.core.NonUniqueResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.playground.springdata.DatabaseCleanup;
import spring.playground.springdata.persistence.OrderStep;
import spring.playground.springdata.persistence.entity.common.Category;
import spring.playground.springdata.persistence.entity.item.Album;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.entity.order.OrderSearch;
import spring.playground.springdata.persistence.entity.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
class OrderQueryDslRepositoryTest {
    private final OrderQueryDslRepository orderQueryDslRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final OrderStep orderStep;
    private final DatabaseCleanup databaseCleanup;

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
        for (int i = 0; i < 10; i++) {
            Member member = orderStep.saveMember("test" + i);
            Album album = orderStep.saveAlbum("album" + i, 1000, 10);
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

}