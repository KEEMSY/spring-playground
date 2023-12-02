package spring.playground.springdata.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import spring.playground.springdata.persistence.entity.ProductEntity;
import spring.playground.springdata.persistence.repository.ProductJpaRepository;
import spring.playground.springdata.persistence.repository.ProductQuerydslRepository;
import spring.playground.springdata.service.dao.ProductDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ProductDAOImpl implements ProductDAO {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQuerydslRepository productQuerydslRepository;

    @Autowired
    public ProductDAOImpl(ProductJpaRepository productJpaRepository,
                          ProductQuerydslRepository productQuerydslRepository) {
        this.productJpaRepository = productJpaRepository;
        this.productQuerydslRepository = productQuerydslRepository;
    }

    @Override
    public ProductEntity insertProduct(ProductEntity product) {
        ProductEntity savedProduct = productJpaRepository.save(product);

        return savedProduct;
    }

    @Override
    public Optional<ProductEntity> selectProduct(Long number) {
        Optional<ProductEntity> selectedProduct = productJpaRepository.findById(number);

        return selectedProduct;
    }

    @Override
    public List<ProductEntity> selectProducts(String stockStatus, Integer minPrice, Integer maxPrice) {
        List<ProductEntity> productEntityList = productQuerydslRepository
                .findProductsByStockStatusAndPrice(stockStatus, minPrice,maxPrice);

        return productEntityList;
    }

    @Override
    public Page<ProductEntity> getAllProducts(Pageable pageable) {
        return productJpaRepository.findAll(pageable);
    }

    @Override
    public ProductEntity updateProductName(Long number, String name) throws Exception {
        Optional<ProductEntity> selectedProduct = productJpaRepository.findById(number);

        ProductEntity updatedProduct;
        if (selectedProduct.isPresent()) {
            ProductEntity product = selectedProduct.get();

            product.setName(name);
            product.setUpdatedAt(LocalDateTime.now());

            updatedProduct = productJpaRepository.save(product);
        } else {
            throw new Exception();
        }

        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long number) throws Exception {
        Optional<ProductEntity> selectedProduct = productJpaRepository.findById(number);

        if (selectedProduct.isPresent()) {
            ProductEntity product = selectedProduct.get();

            productJpaRepository.delete(product);
        } else {
            throw new Exception();
        }
    }
}