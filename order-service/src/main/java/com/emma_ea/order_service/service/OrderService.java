package com.emma_ea.order_service.service;

import com.emma_ea.order_service.dto.OrderLineItemsDto;
import com.emma_ea.order_service.dto.OrderRequest;
import com.emma_ea.order_service.dto.OrderResponse;
import com.emma_ea.order_service.model.Order;
import com.emma_ea.order_service.model.OrderLineItems;
import com.emma_ea.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public void createOrder(OrderRequest orderRequest) {
        Order order = buildOrder(orderRequest);
        orderRepository.save(order);
        log.info("Order {} created", order.getId());
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
            return new ResponseEntity<>(error, HttpStatus.OK);
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
