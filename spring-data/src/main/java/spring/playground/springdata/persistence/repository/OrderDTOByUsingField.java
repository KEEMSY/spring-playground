package spring.playground.springdata.persistence.repository;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDTOByUsingField {

    private String memberName;
    private String orderStatus;


}
