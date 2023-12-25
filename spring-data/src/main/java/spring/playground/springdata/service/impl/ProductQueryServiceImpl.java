package spring.playground.springdata.service.impl;

import spring.playground.springdata.persistence.entity.ProductEntity;
import spring.playground.springdata.persistence.entity.ProductQueryEntity;
import spring.playground.springdata.persistence.repository.ProductJpaRepository;
import spring.playground.springdata.persistence.repository.ProductMongoRepository;
import spring.playground.springdata.persistence.dao.ProductQueryService;
import spring.playground.springdata.persistence.repository.ProductQuerydslRepository;

import java.util.List;
import java.util.Optional;


public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductMongoRepository productMongoRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductQuerydslRepository productQuerydslRepository;

    public ProductQueryServiceImpl(ProductMongoRepository productMongoRepository,
                                   ProductJpaRepository productJpaRepository,
                                   ProductQuerydslRepository productQuerydslRepository) {
        this.productMongoRepository = productMongoRepository;
        this.productJpaRepository = productJpaRepository;
        this.productQuerydslRepository = productQuerydslRepository;
    }


    @Override
    public Optional<ProductQueryEntity> getProductsByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<ProductQueryEntity> selectProductsEntity() {
        return null;
    }

    @Override
    public Optional<ProductEntity> selectProduct(Long number) {
        return Optional.empty();
    }

    @Override
    public List<ProductEntity> selectProducts(String stockStatus, Integer minPrice, Integer maxPrice) {
        return null;
    }
}
