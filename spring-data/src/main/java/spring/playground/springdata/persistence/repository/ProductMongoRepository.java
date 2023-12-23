package spring.playground.springdata.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.playground.springdata.persistence.entity.ProductQueryEntity;

public interface ProductMongoRepository extends MongoRepository<ProductQueryEntity, String> {
    ProductQueryEntity findByName(String name);
}
