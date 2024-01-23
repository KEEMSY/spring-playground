package spring.playground.springdata.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.playground.springdata.persistence.entity.item.Item;


public interface ItemJpaRepository extends JpaRepository<Item, Long> {
}
