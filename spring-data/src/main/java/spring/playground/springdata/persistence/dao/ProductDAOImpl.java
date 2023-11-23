package spring.playground.springdata.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.playground.springdata.persistence.entity.ProductEntity;
import spring.playground.springdata.persistence.repository.ProductRepository;
import spring.playground.springdata.service.dao.ProductDAO;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ProductDAOImpl implements ProductDAO {

    private ProductRepository productRepository;

    @Autowired
    public ProductDAOImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductEntity insertProduct(ProductEntity product) {
        ProductEntity savedProduct = productRepository.save(product);

        return savedProduct;
    }

    @Override
    public Optional<ProductEntity> selectProduct(Long number) {
        Optional<ProductEntity> selectedProduct = productRepository.findById(number);

        return selectedProduct;
    }

    @Override
    public ProductEntity updateProductName(Long number, String name) throws Exception {
        Optional<ProductEntity> selectedProduct = productRepository.findById(number);

        ProductEntity updatedProduct;
        if (selectedProduct.isPresent()) {
            ProductEntity product = selectedProduct.get();

            product.setName(name);
            product.setUpdatedAt(LocalDateTime.now());

            updatedProduct = productRepository.save(product);
        } else {
            throw new Exception();
        }

        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long number) throws Exception {
        Optional<ProductEntity> selectedProduct = productRepository.findById(number);

        if (selectedProduct.isPresent()) {
            ProductEntity product = selectedProduct.get();

            productRepository.delete(product);
        } else {
            throw new Exception();
        }
    }
}