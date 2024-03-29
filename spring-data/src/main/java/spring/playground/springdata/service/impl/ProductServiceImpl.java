package spring.playground.springdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spring.playground.springdata.persistence.entity.ProductEntity;
import spring.playground.springdata.service.ProductService;
import spring.playground.springdata.service.dao.ProductDAO;
import spring.playground.springdata.service.dto.ListProductResponseDto;
import spring.playground.springdata.service.dto.ProductDto;
import spring.playground.springdata.service.dto.ProductResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    @Autowired
    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public ProductResponseDto getProduct(Long number) {
        ProductEntity product = productDAO.selectProduct(number).orElseThrow();

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setNumber(product.getNumber());
        productResponseDto.setName(product.getName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setStock(product.getStock());

        return productResponseDto;
    }

    @Override
    public ListProductResponseDto getProductsByCondition(String stockStatus, Integer minPrice, Integer maxPrice) {
        List<ProductEntity> productEntityList = productDAO.selectProducts(stockStatus, minPrice, maxPrice);

        List<ProductResponseDto> productResponseDtoList = productEntityList.stream()
                .map(entity -> new ProductResponseDto(entity.getNumber(), entity.getName(), entity.getPrice(), entity.getStock()))
                .collect(Collectors.toList());

        return new ListProductResponseDto(productResponseDtoList);
    }

    @Override
    public ListProductResponseDto getProductsByConditionPage(String stockStatus, Integer minPrice, Integer maxPrice, Pageable pageable) throws Exception {
        Page<ProductEntity> productEntities = productDAO.selectProductsPage(stockStatus, minPrice, maxPrice, pageable);

        List<ProductResponseDto> productResponseDtoList = productEntities.stream()
                .map(entity -> new ProductResponseDto(entity.getNumber(), entity.getName(), entity.getPrice(), entity.getStock()))
                .collect(Collectors.toList());

        return new ListProductResponseDto(productResponseDtoList);
    }

    @Override
    public ProductResponseDto saveProduct(ProductDto productDto) {
        ProductEntity product = new ProductEntity();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        ProductEntity savedProduct = productDAO.insertProduct(product);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setNumber(savedProduct.getNumber());
        productResponseDto.setName(savedProduct.getName());
        productResponseDto.setPrice(savedProduct.getPrice());
        productResponseDto.setStock(savedProduct.getStock());

        return productResponseDto;
    }

    @Override
    public ProductResponseDto changeProductName(Long number, String name) throws Exception {
        ProductEntity changedProduct = productDAO.updateProductName(number, name);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setNumber(changedProduct.getNumber());
        productResponseDto.setName(changedProduct.getName());
        productResponseDto.setPrice(changedProduct.getPrice());
        productResponseDto.setStock(changedProduct.getStock());

        return productResponseDto;
    }

    @Override
    public void deleteProduct(Long number) throws Exception {
        productDAO.deleteProduct(number);
    }
}