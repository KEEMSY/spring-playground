package com.example.springskeleton.adapter.order.api;

import com.example.springskeleton.application.OrderService;
import com.example.springskeleton.domain.order.Order;
import com.example.springskeleton.dto.order.OrderDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@AllArgsConstructor
public class OrderApiController {
    private final OrderService orderService;

    @GetMapping("api/v1/orders")
    public ResponseEntity<List<OrderDto>> getOrders() {
        List<Order> orders = orderService.getOrders();
        List<OrderDto> orderDtoList = orders.stream()
                .map(OrderDto::new)
                .toList();
        log.info("Returning orders: {}", orderDtoList.size());
        return ResponseEntity.ok(orderDtoList);
    }
}
