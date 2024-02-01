package spring.playground.springdata.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.playground.springdata.persistence.entity.common.Address;
import spring.playground.springdata.persistence.entity.common.Category;
import spring.playground.springdata.persistence.entity.item.Album;
import spring.playground.springdata.persistence.entity.item.Item;
import spring.playground.springdata.persistence.entity.member.Member;
import spring.playground.springdata.persistence.repository.MemberJpaRepository;
import spring.playground.springdata.service.OrderPort;

import java.util.ArrayList;

@Component
public class OrderStep {
    @Autowired
    private  MemberJpaRepository memberJpaRepository;
    @Autowired
    private  OrderPort orderAdapter;

    public Member saveMember(String memberName) {
        Member member = new Member();
        member.setName(memberName);
        Address address = new Address("city", "street", "zipcode");
        member.setAddress(address);

        memberJpaRepository.save(member);
        return member;
    }

    public Album saveAlbum(String albumName, int price, int stockQuantity) {
        Album item = new Album();
        item.setName(albumName);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);

        return item;
    }

    public Category addCategory(Item item) {
        Category category = new Category();
        category.setName("Category Name");
        category.setItems(new ArrayList<>());
        category.getItems().add(item);
        return category;
    }

    public void createOrder(Member member, Long itemId) {
        orderAdapter.order(member.getId(), itemId, 1);
    }

}
