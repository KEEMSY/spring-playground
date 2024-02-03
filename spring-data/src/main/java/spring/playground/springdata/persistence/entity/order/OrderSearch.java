package spring.playground.springdata.persistence.entity.order;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

        private String memberName; //회원 이름
        private OrderStatus orderStatus; //주문 상태[ORDER, CANCEL]
        private int limit; // 페이징
        private int offset; // 페이징
}
