package spring.playground.springdata.persistence.dao;

import spring.playground.springdata.persistence.entity.ProductEntity;
import spring.playground.springdata.persistence.entity.ProductQueryEntity;

import java.util.List;
import java.util.Optional;

public interface ProductQueryService {
    Optional<ProductQueryEntity> getProductsByName(String name);
    List<ProductQueryEntity> selectProductsEntity();

    Optional<ProductEntity> selectProduct(Long number);
    List<ProductEntity> selectProducts(String stockStatus, Integer minPrice, Integer maxPrice);
}
