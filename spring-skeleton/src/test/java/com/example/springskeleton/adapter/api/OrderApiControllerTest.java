package com.example.springskeleton.adapter.api;

import com.example.springskeleton.application.OrderService;
import com.example.springskeleton.domain.common.Address;
import com.example.springskeleton.domain.common.Category;
import com.example.springskeleton.domain.common.Delivery;
import com.example.springskeleton.domain.common.DeliveryStatus;
import com.example.springskeleton.domain.item.Album;
import com.example.springskeleton.domain.item.Item;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.domain.order.Order;
import com.example.springskeleton.domain.order.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("정상 Order 조회")
    void getOrdersTest() throws Exception {
        // given
        Address address = createAddress();

        Member member = createMember(address);
        Album album = createAlbum();
        Category category = createCategoryWithItem(album);

        Delivery delivery = createDelivery(address, member);

        OrderItem orderItem = OrderItem.createOrderItem(album, album.getPrice(), 1);
        Order order = Order.createOrder(member, delivery, orderItem);

        given(orderService.getOrders()).willReturn(new ArrayList<>() {{
            add(order);
        }});

        // when, then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/orders")
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderedMemberName").value(member.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderStatus").value(order.getStatus().toString()));

    }

    @Test
    @DisplayName("다수의 동시 Order 요청")
    void getOrders_MultiThread() throws InterruptedException {
        // given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successfulCalls = new AtomicInteger(0);

        // when
        for(int i = 0; i < threadCount; i ++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/v1/orders")
                            .header("Content-Type", "application/json"))
                            .andExpect(MockMvcResultMatchers.status().isOk());

                    successfulCalls.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successfulCalls.intValue()).isEqualTo(threadCount);
    }

    private static Delivery createDelivery(Address address, Member member) {
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);
        delivery.setAddress(address);
        return delivery;
    }

    private static Address createAddress() {
        return new Address("city", "street", "zipcode");
    }

    private static Category createCategoryWithItem(Item item) {
        Category category = new Category();
        category.setName("Category Name");
        category.setItems(new ArrayList<>());
        category.getItems().add(item);
        return category;
    }

    private static Album createAlbum() {
        Album item = new Album();
        item.setName("albumName");
        item.setPrice(10);
        item.setStockQuantity(10);
        return item;
    }

    private static Member createMember(Address address) {
        Member member = new Member();
        member.setName("memberName");
        member.setAddress(address);
        return member;
    }
}