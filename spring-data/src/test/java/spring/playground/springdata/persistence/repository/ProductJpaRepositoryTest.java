package spring.playground.springdata.persistence.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import spring.playground.springdata.persistence.entity.ProductEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class ProductJpaRepositoryTest {
    @Autowired
    ProductJpaRepository productJpaRepository;

    @AfterEach
    public void clean() {
        productJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("페이지 네이션 확인")
    void findAllPaginationTest() {
        // given
        List<ProductEntity> productList = new ArrayList<>();
        ProductEntity soldoutProductEntity = ProductEntity.builder()
                .name("testName0")
                .price(10)
                .stock(0)
                .build();

        ProductEntity InStockProductEntity1 = ProductEntity.builder()
                .name("testName1")
                .price(10)
                .stock(1)
                .build();

        ProductEntity InStockProductEntity2 = ProductEntity.builder()
                .name("testName10")
                .price(100)
                .stock(10)
                .build();

        productList.add(soldoutProductEntity);
        productList.add(InStockProductEntity1);
        productList.add(InStockProductEntity2);

        productJpaRepository.saveAll(productList);

        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);

        // when
        Page<ProductEntity> productEntities = productJpaRepository.findAll(pageable);

        // then
        assertThat(productEntities).isNotNull();
        assertThat(productEntities.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("페이지 네이션 정렬 확인")
    void findAllPaginationSortedTest_ShouldBeSortedBy() {
        // given
        List<ProductEntity> productList = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            ProductEntity productEntity = ProductEntity.builder()
                    .name("testName0")
                    .price(10)
                    .stock(i)
                    .build();

            productList.add(productEntity);
        }

        productJpaRepository.saveAll(productList);

        int page = 0;
        int size = 3;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("stock").descending());

        // when
        Page<ProductEntity> productEntities = productJpaRepository.findAll(pageable);

        // then
        assertThat(productEntities).isNotNull();
        assertThat(productEntities.getContent()).hasSize(3);
    }


}