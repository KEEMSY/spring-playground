package spring.playground.springdata.persistence.repository.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import spring.playground.springdata.persistence.entity.ProductEntity;
import spring.playground.springdata.persistence.repository.ProductJpaRepository;
import spring.playground.springdata.persistence.repository.ProductQuerydslRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductQuerydslRepositoryImplTest {
    @Autowired
    ProductQuerydslRepository productQuerydslRepository;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @AfterEach
    public void clean() {
        productJpaRepository.deleteAll();
    }

    @Test
    void findProductsByStockStatusAndPriceTest() {
        // given
        List<ProductEntity> productList = new ArrayList<>();
        ProductEntity soldoutProductEntity = ProductEntity.builder()
                .name("testName")
                .price(10)
                .stock(0)
                .build();

        ProductEntity InStockProductEntity1 = ProductEntity.builder()
                .name("testName")
                .price(10)
                .stock(10)
                .build();

        ProductEntity InStockProductEntity2 = ProductEntity.builder()
                .name("testName")
                .price(100)
                .stock(10)
                .build();

        String stockStatus = "IN-STOCK";
        Integer minPrice = 1;
        Integer maxPrice = 10;

        productList.add(soldoutProductEntity);
        productList.add(InStockProductEntity1);
        productList.add(InStockProductEntity2);

        productJpaRepository.saveAll(productList);

        // when
        List<ProductEntity> productEntityList = productQuerydslRepository
                .findProductsByStockStatusAndPrice(stockStatus, minPrice, maxPrice);


        // then
        assertThat(productEntityList).isNotEmpty();
        assertThat(productEntityList.size()).isEqualTo(1);
    }

    @Test
    void findProductsByStockStatusAndPricePageTest() {
        // given
        List<ProductEntity> productList = new ArrayList<>();
        ProductEntity soldoutProductEntity = ProductEntity.builder()
                .name("testName")
                .price(10)
                .stock(0)
                .build();

        ProductEntity InStockProductEntity1 = ProductEntity.builder()
                .name("testName")
                .price(10)
                .stock(10)
                .build();

        ProductEntity InStockProductEntity2 = ProductEntity.builder()
                .name("testName")
                .price(100)
                .stock(10)
                .build();

        String stockStatus = "IN-STOCK";
        Integer minPrice = 1;
        Integer maxPrice = 10;

        productList.add(soldoutProductEntity);
        productList.add(InStockProductEntity1);
        productList.add(InStockProductEntity2);

        productJpaRepository.saveAll(productList);

        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);

        // when
        Page<ProductEntity> productEntityList = productQuerydslRepository
                .findProductsByStockStatusAndPricePage(stockStatus, minPrice, maxPrice, pageable);


        // then
        assertThat(productEntityList).isNotEmpty();
        assertThat(productEntityList.getContent()).hasSize(1); // Assuming two products match the criteria

        // You can further assert specific properties of the returned products if needed
        for (ProductEntity productEntity : productEntityList.getContent()) {
            assertThat(productEntity.getStock()).isGreaterThan(0);
            assertThat(productEntity.getPrice()).isBetween(minPrice, maxPrice);
        }
    }

}