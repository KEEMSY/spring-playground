package spring.playground.springdata.persistence.repository;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;


@Data
public class OrderDTOByUsingQueryProjection {

    private String memberName;
    private String orderStatus;

    @QueryProjection
    public OrderDTOByUsingQueryProjection(String memberName, String orderStatus) {
        this.memberName = memberName;
        this.orderStatus = orderStatus;
    }
}




