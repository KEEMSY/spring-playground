package spring.playground.springdata.persistence.entity.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import spring.playground.springdata.exception.NotEnoughStockException;
import spring.playground.springdata.persistence.entity.common.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderItem 와 Item 의 관계는 ToOne 이며, default_batch_fetch_size 옵션을 개별적으로 적용하고 싶은 경우(ToOne)
 * @BatchSize(size = ) 어노테이션을 필드명이 아닌, Class 위에 붙여야 한다.
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("상품의 재고가 부족합니다.");
        }
        this.stockQuantity = restStock;
    }
}