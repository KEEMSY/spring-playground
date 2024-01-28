package spring.playground.springdata.service.adapter;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.playground.springdata.exception.NotEnoughStockException;
import spring.playground.springdata.persistence.entity.common.Address;
import spring.playground.springdata.persistence.entity.common.Category;
import spring.playground.springdata.persistence.entity.item.Album;
import spring.playground.springdata.persistence.entity.item.Item;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.entity.order.DeliveryStatus;
import spring.playground.springdata.persistence.entity.order.Order;
import spring.playground.springdata.persistence.entity.order.OrderStatus;
import spring.playground.springdata.persistence.repository.ItemJpaRepository;
import spring.playground.springdata.persistence.repository.MemberJpaRepository;
import spring.playground.springdata.persistence.repository.OrderJpaRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.ArrayList;

@SpringBootTest
class OrderAdapterTest {
    private final OrderAdapter orderAdapter;
    private final MemberJpaRepository memberJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final OrderJpaRepository orderJpaRepository;

    @Autowired
    OrderAdapterTest(OrderAdapter orderAdapter,
                     MemberJpaRepository memberJpaRepository,
                     ItemJpaRepository itemJpaRepository,
                     OrderJpaRepository orderJpaRepository) {
        this.orderAdapter = orderAdapter;
        this.memberJpaRepository = memberJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
        this.orderJpaRepository = orderJpaRepository;
    }

    @BeforeEach
    void setUp() {
        memberJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
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

    @NotNull
    private static Category addCategory(Item album) {
        Category category = new Category();
        category.setName("Category Name");
        category.setItems(new ArrayList<>());
        category.getItems().add(album);
        return category;
    }

    @Test
    void cancelOrder() {
    }

    @Test
    void findOrders() {
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
}