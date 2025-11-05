package com.utkarsh.order_service.service;

import com.utkarsh.order_service.dto.InventoryResponse;
import com.utkarsh.order_service.dto.OrderLineItemDto;
import com.utkarsh.order_service.dto.OrderRequest;
import com.utkarsh.order_service.model.Order;
import com.utkarsh.order_service.model.OrderLineItem;
import com.utkarsh.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        // 1. Create order object
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        // 2. Map order request to order object

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream()
                .map(this::mapToOrderLineItem)
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLineItems);

        // 3. Map order line items to order line item objects

        List<String> skuCodes = order.getOrderItems().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        log.info("Checking inventory for skuCodes: {}", skuCodes);

        // 4. Call InventoryService using POST and sending skuCodes in the body

        // Call InventoryService using POST and sending skuCodes in the body
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().post()
                .uri("http://inventory-service/api/inventory") // The endpoint is now a POST
                .bodyValue(skuCodes) // Send the list as the request body
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block(); // Still synchronous

        // 5. Check if all products are in stock

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        // 6. If all products are in stock, save the order

        if (allProductsInStock) {
            orderRepository.save(order);
            log.info("Order {} placed successfully", order.getOrderNumber());
        } else {
            log.error("Not all products are in stock, order failed.");
            throw new IllegalArgumentException("One or more products are not in stock. Please try again later.");
        }
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemDto dto) {
        return OrderLineItem.builder()
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .skuCode(dto.getSkuCode())
                .build();
    }

}
