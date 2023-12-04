package spring.playground.springdata.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spring.playground.springdata.persistence.entity.ProductEntity;

import java.util.List;


public interface ProductQuerydslRepository {
    List<ProductEntity> findProductsByStockStatusAndPrice(String stockStatus, Integer minPrice, Integer madPrice);
    Page<ProductEntity> findProductsByStockStatusAndPricePage(String stockStatus, Integer minPrice, Integer madPrice, Pageable pageable);
}
