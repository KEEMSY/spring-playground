package spring.playground.springdata.persistence.repository;

import spring.playground.springdata.persistence.entity.ProductEntity;

import java.util.List;


public interface ProductQuerydslRepository {
    List<ProductEntity> findProductsByStockStatusAndPrice(String stockStatus, Integer minPrice, Integer madPrice);
}
