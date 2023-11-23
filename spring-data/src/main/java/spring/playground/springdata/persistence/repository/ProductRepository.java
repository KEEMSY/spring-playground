package spring.playground.springdata.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.playground.springdata.persistence.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}

