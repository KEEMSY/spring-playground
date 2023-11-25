package spring.playground.springdata.service;

import spring.playground.springdata.service.dto.ListProductResponseDto;
import spring.playground.springdata.service.dto.ProductDto;
import spring.playground.springdata.service.dto.ProductResponseDto;

public interface ProductService {

    ProductResponseDto getProduct(Long number);

    ListProductResponseDto getProductsByCondition(String stockStatus, Integer minPrice, Integer maxPrice) throws Exception;

    ProductResponseDto saveProduct(ProductDto productDto);

    ProductResponseDto changeProductName(Long number, String name) throws Exception;

    void deleteProduct(Long number) throws Exception;

}