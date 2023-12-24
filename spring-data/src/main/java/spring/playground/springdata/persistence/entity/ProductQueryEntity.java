package spring.playground.springdata.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection="product")
public class ProductQueryEntity {
    private  String name;
    private int price;
}
