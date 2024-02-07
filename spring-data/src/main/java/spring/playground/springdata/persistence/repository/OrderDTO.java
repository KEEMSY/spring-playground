package spring.playground.springdata.persistence.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import spring.playground.springdata.persistence.entity.order.OrderStatus;

/**
 * Default 생성자를 넣거나 @NoArgsConstructor 를 사용 해야함
 * fetchOrdersWithSetter() 메소드에서 OrderDTOByUsingSetter 를 사용하고 있음
 * Entity 내 선언된 필드 명을 동일하게 해야함
 */
@Data
@NoArgsConstructor
public class OrderDTO {
    private String name;
    private OrderStatus orderStatus;

//    public OrderDTOByUsingSetter() {
//    }

    public OrderDTO(String name, OrderStatus orderStatus) {
        this.name = name;
        this.orderStatus = orderStatus;
    }
}
