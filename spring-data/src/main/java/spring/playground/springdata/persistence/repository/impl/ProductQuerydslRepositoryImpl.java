package spring.playground.springdata.persistence.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import spring.playground.springdata.persistence.entity.ProductEntity;
import spring.playground.springdata.persistence.repository.ProductQuerydslRepository;

import java.util.List;

import static spring.playground.springdata.persistence.entity.QProductEntity.productEntity;

@Repository
public class ProductQuerydslRepositoryImpl implements ProductQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductQuerydslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<ProductEntity> findProductsByStockStatusAndPrice(String stockStatus, Integer minPrice, Integer maxPrice) {
        return jpaQueryFactory
            .select(productEntity)
            .from(productEntity)
                .where(
                        priceBetween(minPrice, maxPrice),
                        stockStatus(stockStatus)
                )
            .fetch();
    }

    private Predicate stockStatus(String stockStatus) {
        return stockStatus.equals("SOLD-OUT") ? productEntity.stock.eq(0) : productEntity.stock.gt(1);
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
        return productEntity.price.between(
                minPrice, maxPrice
        );
    }
}
