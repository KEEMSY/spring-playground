package spring.playground.springdata.service.dao;

import spring.playground.springdata.persistence.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {

    ProductEntity insertProduct(ProductEntity productEntity);

    Optional<ProductEntity> selectProduct(Long number);

    List<ProductEntity> selectProducts(String stockStatus, Integer minPrice, Integer maxPrice);

    ProductEntity updateProductName(Long number, String name) throws Exception;

    void deleteProduct(Long number) throws Exception;

}
