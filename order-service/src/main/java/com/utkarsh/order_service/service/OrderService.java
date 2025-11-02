package com.utkarsh.order_service.service;

import com.utkarsh.order_service.dto.OrderLineItemDto;
import com.utkarsh.order_service.dto.OrderRequest;
import com.utkarsh.order_service.model.Order;
import com.utkarsh.order_service.model.OrderLineItem;
import com.utkarsh.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream()
                .map(this::mapToOrderLineItem)
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLineItems);
        orderRepository.save(order);
        log.info("Order {} placed successfully", order.getOrderNumber());
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemDto dto) {
        return OrderLineItem.builder()
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .skuCode(dto.getSkuCode())
                .build();
    }

}
