package com.example.springskeleton.dto;

import com.example.springskeleton.domain.common.Address;
import com.example.springskeleton.domain.order.Order;
import com.example.springskeleton.domain.order.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class OrderDto {

    private Long orderId;
    private String orderedMemberName;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        orderId = order.getId();
        orderedMemberName = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
        orderItems = order.getOrderItems().stream() // orderItemEntity 팔요한 부분만 갖도록 해야함
                .map(orderItem -> new OrderItemDto(orderItem))
                .collect(toList());
    }
}
