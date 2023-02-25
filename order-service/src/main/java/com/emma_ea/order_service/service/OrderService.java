package com.emma_ea.order_service.service;

import com.emma_ea.order_service.dto.*;
import com.emma_ea.order_service.events.OrderPlacedEvent;
import com.emma_ea.order_service.model.Order;
import com.emma_ea.order_service.model.OrderLineItems;
import com.emma_ea.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private final Tracer tracer;

    public String createOrder(OrderRequest orderRequest) {
        Order order = buildOrder(orderRequest);

        Span inventorySpan = tracer.nextSpan().name("InventoryServiceLookUp");
        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventorySpan.start())) {
            boolean inStock = checkOrderInventory(order);
            if (inStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                log.info("Order {} created", order.getId());
                return "Order created";
            }
            log.info("Order {} failed", order.getId());
            return "Order failed";
        } finally {
            inventorySpan.end();
        }
    }

    public boolean checkOrderInventory(Order order) {
        List<InventoryRequest> inventory = order.getOrderLineItemsList()
                .stream()
                .map(orderLine -> new InventoryRequest(orderLine.getSkuCode(), orderLine.getQuantity()))
                .collect(Collectors.toUnmodifiableList());

        List<String> skuCodes = inventory.stream().map(InventoryRequest::getSkuCode).collect(Collectors.toUnmodifiableList());

        List<InventoryResponse> response = Arrays.asList(Objects.requireNonNull(
                webClient.build().get()
                        .uri("http://inventory-service/api/inventory",
                                uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                        .retrieve()
                        .bodyToMono(InventoryResponse[].class)
                        .block()
                ));

        log.info(response.toString());

        if (response.isEmpty()) return false;

        return response.stream().allMatch(InventoryResponse::isInStock);
    }

    private Order buildOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest
                .getOrderLineItemsDtoList()
                .stream()
                .map(this::orderLineItemsMapper)
                .collect(Collectors.toUnmodifiableList());
        order.setOrderLineItemsList(orderLineItems);
        return order;
    }

    private OrderLineItems orderLineItemsMapper(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }

    public List<OrderResponse> orders() {
        return orderRepository.findAll()
                .stream().map(this::ordersToResponseMapper)
                .collect(Collectors.toUnmodifiableList());
    }

    public ResponseEntity<String> delete(String id) {
        try {
            Order order = orderRepository.findByOrderNumber(id).orElseThrow();
            orderRepository.deleteById(order.getId());
            return new ResponseEntity<>("Order deleted.", HttpStatus.OK);
        } catch (Exception e) {
            String error = String.format("Order %s failed to be deleted. %s", id, e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    private OrderResponse ordersToResponseMapper(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setOrderLineItemsDtoList(order.getOrderLineItemsList()
                .stream()
                .map(this::orderLineItemsDtoMapper)
                .collect(Collectors.toUnmodifiableList())
        );
        return orderResponse;
    }

    private OrderLineItemsDto orderLineItemsDtoMapper(OrderLineItems orderLine) {
        OrderLineItemsDto orderLineItemsDto = new OrderLineItemsDto();
        orderLineItemsDto.setPrice(orderLine.getPrice());
        orderLineItemsDto.setSkuCode(orderLine.getSkuCode());
        orderLineItemsDto.setQuantity(orderLine.getQuantity());
        return orderLineItemsDto;
    }
}
