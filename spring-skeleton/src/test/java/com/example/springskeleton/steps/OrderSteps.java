package com.example.springskeleton.steps;

import com.example.springskeleton.adapter.order.persistence.ItemJpaRepository;
import com.example.springskeleton.adapter.member.persistence.MemberJpaRepository;
import com.example.springskeleton.domain.common.Address;
import com.example.springskeleton.domain.common.Category;
import com.example.springskeleton.domain.common.Delivery;
import com.example.springskeleton.domain.common.DeliveryStatus;
import com.example.springskeleton.domain.item.Album;
import com.example.springskeleton.domain.item.Item;
import com.example.springskeleton.domain.member.Member;
import com.example.springskeleton.domain.member.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
public class OrderSteps {

    @Autowired
    private final ItemJpaRepository itemJpaRepository;
    @Autowired
    private final MemberJpaRepository memberJpaRepository;

    public OrderSteps(ItemJpaRepository itemJpaRepository,
                      MemberJpaRepository memberJpaRepository) {
        this.itemJpaRepository = itemJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    public Delivery createDelivery(Address address, Member member) {
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);
        delivery.setAddress(address);
        return delivery;
    }

    public Address createAddress() {
        return new Address("city", "street", "zipcode");
    }

    public Category createCategoryWithItem(Item item) {
        Category category = new Category();
        category.setName("Category Name");
        category.setItems(new ArrayList<>());
        category.getItems().add(item);
        return category;
    }

    public Album createAlbum() {
        Album item = new Album();
        item.setName("albumName");
        item.setPrice(10);
        item.setStockQuantity(10);

        itemJpaRepository.save(item);
        return item;
    }

    public Member createMember(Address address) {
        Member member = new Member();
        member.setName("memberName");
        member.setEmail("Test@email.com");
        member.setPassword("securePassword");
        member.setAddress(address);
        member.setRoles(Set.of(Role.USER));

        memberJpaRepository.save(member);
        return member;
    }
}
