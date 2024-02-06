package spring.playground.springdata.persistence.repository;

import lombok.Data;
import spring.playground.springdata.persistence.entity.order.OrderStatus;

/**
 * Default 생성자를 넣거나 @NoArgsConstructor 를 사용 해야함
 * Setter 를 통해 값을 지정하는 방법임
 * Entity 내 선언된 필드 명을 동일하게 해야함
 */
@Data
public class OrderDTOByUsingSetter {
    private String name;
    private OrderStatus orderStatus;

    public OrderDTOByUsingSetter() {
    }

    public OrderDTOByUsingSetter(String name, OrderStatus orderStatus) {
        this.name = name;
        this.orderStatus = orderStatus;
    }
}
