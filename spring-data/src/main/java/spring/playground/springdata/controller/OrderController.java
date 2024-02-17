package spring.playground.springdata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.playground.springdata.persistence.entity.order.OrderSearch;
import spring.playground.springdata.persistence.repository.OrderDTOByUsingQueryProjection;
import spring.playground.springdata.service.OrderPort;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderPort orderPort;

    // deprecated 된 방법 이므로 사용하지 않도록 하자.
//    @GetMapping("/v1/orders")
//    public List<OrderDTOByUsingQueryProjection> searchOrdersV1(OrderSearch orderSearch, Pageable pageable) {
//        return orderPort.searchPageSimple(orderSearch, pageable).getContent();
//    }

    @GetMapping("/v2/orders")
    public List<OrderDTOByUsingQueryProjection> searchOrdersV2(OrderSearch orderSearch, Pageable pageable) {
        return orderPort.searchPageComplex(orderSearch, pageable).getContent();
    }

}
