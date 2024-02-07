package spring.playground.springdata.persistence.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import spring.playground.springdata.persistence.entity.order.OrderStatus;

@Data
@NoArgsConstructor
public class OrderDTOWithConstructor {

    private String memberName;
    private OrderStatus orderStatus;

    public OrderDTOWithConstructor(String name, OrderStatus orderStatus) {
        this.memberName = name;
        this.orderStatus = orderStatus;
    }
}
