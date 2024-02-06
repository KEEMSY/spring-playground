package spring.playground.springdata.persistence.repository;

import lombok.Data;
import spring.playground.springdata.persistence.entity.order.OrderStatus;

@Data
public class OrderDTO {
    private OrderStatus orderStatus;
    private String memberName;
    private int orderItemAmount;

    public OrderDTO() {
    }

    public OrderDTO(OrderStatus orderStatus, String memberName, int orderItemAmount) {
        this.orderStatus = orderStatus;
        this.memberName = memberName;
        this.orderItemAmount = orderItemAmount;
    }
}
