package spring.playground.springdata.service.dao;

import spring.playground.springdata.persistence.entity.ProductEntity;

import java.util.Optional;

public interface ProductDAO {

    ProductEntity insertProduct(ProductEntity productEntity);

    Optional<ProductEntity> selectProduct(Long number);

    ProductEntity updateProductName(Long number, String name) throws Exception;

    void deleteProduct(Long number) throws Exception;

}
