package com.utkarsh.order_service.service;

import com.utkarsh.order_service.dto.InventoryResponse;
import com.utkarsh.order_service.dto.OrderLineItemDto;
import com.utkarsh.order_service.dto.OrderRequest;
import com.utkarsh.order_service.model.Order;
import com.utkarsh.order_service.model.OrderLineItem;
import com.utkarsh.order_service.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
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
    private final Tracer tracer;

    public String placeOrder(OrderRequest orderRequest) {
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

        // Create a new span named "InventoryServiceLookup"
        Span inventoryServiceLookupSpan = tracer.nextSpan().name("InventoryServiceLookup");

        // Start the span and add it to the current scope
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookupSpan.start())) {

            // This is your original WebClient call
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().post()
                    .uri("http://inventory-service/api/inventory")
                    .bodyValue(skuCodes)
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
// === New Tracing Code END ===

            boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                    .allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                log.info("Order {} placed successfully", order.getOrderNumber());
                return "Order Placed Successfully";
            } else {
                log.error("Not all products are in stock, order failed.");
                throw new IllegalArgumentException("One or more products are not in stock. Please try again later.");
            }

        } finally {
            // End the span when the block is finished
            inventoryServiceLookupSpan.end();
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
