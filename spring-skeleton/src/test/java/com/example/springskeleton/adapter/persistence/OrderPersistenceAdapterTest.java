package com.example.springskeleton.adapter.persistence;

import com.example.springskeleton.DatabaseCleanUp;
import com.example.springskeleton.adapter.order.persistence.OrderJpaRepository;
import com.example.springskeleton.adapter.order.persistence.OrderPersistenceAdapter;
import com.example.springskeleton.domain.common.Address;
import com.example.springskeleton.domain.common.Category;
import com.example.springskeleton.domain.common.Delivery;
import com.example.springskeleton.domain.item.Album;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.domain.order.Order;
import com.example.springskeleton.domain.order.OrderItem;
import com.example.springskeleton.steps.OrderSteps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class OrderPersistenceAdapterTest {
    private final OrderPersistenceAdapter orderPersistenceAdapter;
    private final OrderJpaRepository orderJpaRepository;
    private final DatabaseCleanUp databaseCleanup;
    private final OrderSteps orderSteps;

    @Autowired
    OrderPersistenceAdapterTest(OrderPersistenceAdapter orderPersistenceAdapter,
                                OrderJpaRepository orderJpaRepository,
                                DatabaseCleanUp databaseCleanup,
                                OrderSteps orderSteps) {
        this.orderPersistenceAdapter = orderPersistenceAdapter;
        this.orderJpaRepository = orderJpaRepository;
        this.databaseCleanup = databaseCleanup;
        this.orderSteps = orderSteps;
    }

    @AfterEach
    void tearDown() {
        databaseCleanup.execute();
    }

    @Test
    @DisplayName("상품조회")
    void getOrders() {
        // given
        Address address = orderSteps.createAddress();
        Member member = orderSteps.createMember(address);
        Album album = orderSteps.createAlbum();
        Category category = orderSteps.createCategoryWithItem(album);
        Delivery delivery = orderSteps.createDelivery(address, member);
        OrderItem orderItem = OrderItem.createOrderItem(album, album.getPrice(), 1);


        orderJpaRepository.saveAll(List.of(
                Order.createOrder(member, delivery, orderItem)
        ));

        // when
        List<Order> orders = orderPersistenceAdapter.findAll();

        // then
        assertThat(orders).isNotEmpty();
        assertThat(orders.size()).isEqualTo(1);

    }
}