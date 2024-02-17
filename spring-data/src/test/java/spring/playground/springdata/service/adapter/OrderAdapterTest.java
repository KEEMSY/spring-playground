package spring.playground.springdata.service.adapter;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import spring.playground.springdata.DatabaseCleanup;
import spring.playground.springdata.exception.NotEnoughStockException;
import spring.playground.springdata.persistence.entity.common.Address;
import spring.playground.springdata.persistence.entity.common.Category;
import spring.playground.springdata.persistence.entity.item.Album;
import spring.playground.springdata.persistence.entity.item.Item;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.entity.order.DeliveryStatus;
import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.entity.order.OrderSearch;
import spring.playground.springdata.persistence.entity.order.OrderStatus;
import spring.playground.springdata.persistence.repository.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class OrderAdapterTest {
    private final OrderAdapter orderAdapter;
    private final MemberJpaRepository memberJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final DatabaseCleanup databaseCleanup;

    @Autowired
    OrderAdapterTest(OrderAdapter orderAdapter,
                     MemberJpaRepository memberJpaRepository,
                     ItemJpaRepository itemJpaRepository,
                     OrderJpaRepository orderJpaRepository,
                     DatabaseCleanup databaseCleanup) {
        this.orderAdapter = orderAdapter;
        this.memberJpaRepository = memberJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
        this.orderJpaRepository = orderJpaRepository;
        this.databaseCleanup = databaseCleanup;
    }

    @AfterEach
    void cleanUp() {
        databaseCleanup.execute();
    }

    @Test
    @DisplayName("주문 성공 테스트")
    @Transactional
    void order() {
        // given
        Member member = saveMember("test");
        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);

        itemJpaRepository.save(album);

        // when
        Long orderId = orderAdapter.order(member.getId(), album.getId(), 1);

        // then
        Order expected_order = orderJpaRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("주문이 존재하지 않습니다."));
        assertThat(member.getOrders().size()).isEqualTo(1);
        
        assertThat(expected_order.getDelivery().getStatus()).isEqualTo(DeliveryStatus.READY);
        assertThat(expected_order.getOrderItems().size()).isEqualTo(1);
        assertThat(expected_order.getOrderItems().get(0).getItem().getName()).isEqualTo("Album Title");
        assertThat(expected_order.getOrderItems().get(0).getItem().getPrice()).isEqualTo(20);
        assertThat(expected_order.getOrderItems().get(0).getItem().getStockQuantity()).isEqualTo(49);
        assertThat(expected_order.getOrderItems().get(0).getCount()).isEqualTo(1);
        assertThat(expected_order.getOrderItems().get(0).getTotalPrice()).isEqualTo(20);
        assertThat(expected_order.getOrderItems().get(0).getOrderPrice()).isEqualTo(20);
        assertThat(expected_order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(expected_order.getOrderDate()).isNotNull();

        assertThat(member.getOrders().get(0).getOrderItems().size()).isEqualTo(1);
        assertThat(member.getOrders().get(0).getOrderItems().get(0).getItem().getName()).isEqualTo("Album Title");
        assertThat(member.getOrders().get(0).getOrderItems().get(0).getItem().getPrice()).isEqualTo(20);
        assertThat(member.getOrders().get(0).getOrderItems().get(0).getItem().getStockQuantity()).isEqualTo(49);

    }

    @Test
    @DisplayName("주문 실패 테스트: 재고 부족")
    void orderFailTest() {
        // given
        Member member = saveMember("test");
        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(0);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);

        itemJpaRepository.save(album);

        // when, then
        assertThatThrownBy(() ->
                orderAdapter.order(member.getId(), album.getId(), 1))
                .isInstanceOf(NotEnoughStockException.class).hasMessage("상품의 재고가 부족합니다.");
    }

    @Test
    @DisplayName("주문 실패 테스트: 존재하지 않는 회원")
    void orderFailTest2() {
        // given
        Member member = saveMember("test");
        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);

        itemJpaRepository.save(album);

        // when, then
        Long unknownMember = member.getId() + 1;
        assertThatThrownBy(() ->
                orderAdapter.order(unknownMember, album.getId(), 1))
                .isInstanceOf(IllegalStateException.class).hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("주문 실패 테스트: 존재하지 않는 상품")
    void orderFailTest3() {
        // given
        Member member = saveMember("test");
        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);

        itemJpaRepository.save(album);

        // when, then
        Long unknownItem = album.getId() + 1;
        assertThatThrownBy(() ->
                orderAdapter.order(member.getId(), unknownItem, 1))
                .isInstanceOf(IllegalStateException.class).hasMessage("존재하지 않는 상품입니다.");
    }

    @Test
    @Transactional
    @DisplayName("주문 취소 테스트")
    void cancelOrder() {
        // given
        Member member = saveMember("test");
        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);

        itemJpaRepository.save(album);

        Long orderId = orderAdapter.order(member.getId(), album.getId(), 1);

        // when
        orderAdapter.cancelOrder(orderId);

        // then
        Order expected_order = orderJpaRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("주문이 존재하지 않습니다."));
        assertThat(expected_order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(expected_order.getOrderItems().get(0).getItem().getStockQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("주문 취소 실패 테스트: 존재하지 않는 주문")
    void orderCancelFail() {
        // given
        Member member = saveMember("test");
        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);

        itemJpaRepository.save(album);

        Long orderId = orderAdapter.order(member.getId(), album.getId(), 1);

        // when, then
        Long unknownOrder = orderId + 1;
        assertThatThrownBy(() ->
                orderAdapter.cancelOrder(unknownOrder))
                .isInstanceOf(IllegalStateException.class).hasMessage("존재하지 않는 주문입니다.");
    }

    @Test
    @Transactional
    @DisplayName("주문 취소 실패 테스트: 이미 배송 완료된 상품")
    void orderCancelFailTest2() {
        // given
        Member member = saveMember("test");
        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album.getCategories().add(category);

        itemJpaRepository.save(album);

        Long orderId = orderAdapter.order(member.getId(), album.getId(), 1);

        // when
        Order order = orderJpaRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("주문이 존재하지 않습니다."));
        order.getDelivery().setStatus(DeliveryStatus.COMP);

        // then
        assertThatThrownBy(() ->
                orderAdapter.cancelOrder(orderId))
                .isInstanceOf(IllegalStateException.class).hasMessage("이미 배송완료된 상품은 취소가 불가능합니다.");
    }

    @Test
    @DisplayName("주문 조회 테스트")
    @Transactional
    void findOrders() {
        // given
        Member member = saveMember("test");

        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Album album2 = new Album();
        album2.setName("Album Title2");
        album2.setPrice(20);
        album2.setStockQuantity(50);
        album2.setArtist("Artist Name2");
        album2.setEtc("Other Album Details2");


        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album2.setCategories(new ArrayList<>());

        album.getCategories().add(category);
        album2.getCategories().add(category);

        itemJpaRepository.save(album);

        Long orderId = orderAdapter.order(member.getId(), album.getId(), 1);
        Long orderId2 = orderAdapter.order(member.getId(), album.getId(), 1);

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("test");

        // when
        List<Order> orders = orderAdapter.findOrders(orderSearch);

        // then
        assertThat(orders.size()).isEqualTo(2);
        assertThat(orders.get(0).getOrderItems().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Pageable 주문 조회 테스트")
    @Disabled
    void searchPageSimple() {
        // given
        Member member = saveMember("test");

        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Album album2 = new Album();
        album2.setName("Album Title2");
        album2.setPrice(20);
        album2.setStockQuantity(50);
        album2.setArtist("Artist Name2");
        album2.setEtc("Other Album Details2");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album2.setCategories(new ArrayList<>());

        album.getCategories().add(category);
        album2.getCategories().add(category);

        itemJpaRepository.save(album);

        orderAdapter.order(member.getId(), album.getId(), 1);
        orderAdapter.order(member.getId(), album.getId(), 1);

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        Page<OrderDTOByUsingQueryProjection> orders = orderAdapter.searchPageComplex(orderSearch, PageRequest.of(0, 10));
        List<OrderDTOByUsingQueryProjection> content = orders.getContent();
        long totalElements = orders.getTotalElements();

        // then
        assertThat(content.size()).isEqualTo(2);
        assertThat(totalElements).isEqualTo(2);

    }

    @Test
    @DisplayName("Pageable 주문 조회 테스트: 복잡한 페이징")
    void searchPageComplex() {
        // given
        Member member = saveMember("test");

        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Album album2 = new Album();
        album2.setName("Album Title2");
        album2.setPrice(20);
        album2.setStockQuantity(50);
        album2.setArtist("Artist Name2");
        album2.setEtc("Other Album Details2");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album2.setCategories(new ArrayList<>());

        album.getCategories().add(category);
        album2.getCategories().add(category);

        itemJpaRepository.save(album);

        orderAdapter.order(member.getId(), album.getId(), 1);
        orderAdapter.order(member.getId(), album.getId(), 1);

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        Page<OrderDTOByUsingQueryProjection> orders = orderAdapter.searchPageComplex(orderSearch, PageRequest.of(0, 10));
        List<OrderDTOByUsingQueryProjection> content = orders.getContent();
        long totalElements = orders.getTotalElements();

        // then
        assertThat(content.size()).isEqualTo(2);
        assertThat(totalElements).isEqualTo(2);
    }

    @Test
    @DisplayName("Pageable 주문 조회 테스트: 카운트 쿼리 최적화")
    void searchPageWithOptimizeCountQuery() {
        // given
        Member member = saveMember("test");

        Album album = new Album();
        album.setName("Album Title");
        album.setPrice(20);
        album.setStockQuantity(50);
        album.setArtist("Artist Name");
        album.setEtc("Other Album Details");

        Album album2 = new Album();
        album2.setName("Album Title2");
        album2.setPrice(20);
        album2.setStockQuantity(50);
        album2.setArtist("Artist Name2");
        album2.setEtc("Other Album Details2");

        Category category = addCategory(album);

        album.setCategories(new ArrayList<>());
        album2.setCategories(new ArrayList<>());

        album.getCategories().add(category);
        album2.getCategories().add(category);

        itemJpaRepository.save(album);

        orderAdapter.order(member.getId(), album.getId(), 1);
        orderAdapter.order(member.getId(), album.getId(), 1);

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);

        // when
        Page<OrderDTOByUsingQueryProjection> orders = orderAdapter.searchPageWithOptimizeCountQuery(orderSearch, PageRequest.of(0, 10));
        List<OrderDTOByUsingQueryProjection> content = orders.getContent();
        long totalElements = orders.getTotalElements();

        // then
        assertThat(content.size()).isEqualTo(2);
        assertThat(totalElements).isEqualTo(2);
    }

    @NotNull
    private Member saveMember(String memberName) {
        Member member = new Member();
        member.setName(memberName);
        Address address = new Address("city", "street", "zipcode");
        member.setAddress(address);

        memberJpaRepository.save(member);
        return member;
    }

    @NotNull
    private static Category addCategory(Item album) {
        Category category = new Category();
        category.setName("Category Name");
        category.setItems(new ArrayList<>());
        category.getItems().add(album);
        return category;
    }
}