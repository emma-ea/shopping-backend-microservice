package com.emma_ea.order_service.service;

import com.emma_ea.order_service.dto.OrderLineItemsDto;
import com.emma_ea.order_service.dto.OrderRequest;
import com.emma_ea.order_service.model.Order;
import com.emma_ea.order_service.model.OrderLineItems;
import com.emma_ea.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private final OrderRepository orderRepositoryMock = Mockito.mock(OrderRepository.class);

    @Autowired
    private WebClient webClient;

    private final OrderService orderServiceMock = Mockito.mock(
            OrderService.class, Mockito.withSettings().useConstructor(orderRepositoryMock, webClient));

    @Test
    @DisplayName("Create order should be verified if invoked")
    void createOrder() {

        orderServiceMock.createOrder(makeOrder());

        Mockito.verify(orderServiceMock, Mockito.times(1)).createOrder(makeOrder());

    }

    @Test
    void deleteOrder() {
        OrderService service = new OrderService(orderRepositoryMock, webClient);

        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber("111-111");

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(1L);
        orderLineItems.setPrice(BigDecimal.valueOf(100));
        orderLineItems.setSkuCode("Test Item");
        orderLineItems.setQuantity(1);

        List<OrderLineItems> items = new ArrayList<>();
        items.add(orderLineItems);

        order.setOrderLineItemsList(items);

        orderServiceMock.delete("111-111");

        Mockito.when(orderRepositoryMock.findByOrderNumber(order.getOrderNumber())).thenReturn(Optional.of(order));
        Mockito.verify(orderServiceMock, Mockito.times(1)).delete("111-111");
    }

    private OrderRequest makeOrder() {
        OrderLineItemsDto orderLineItemsDto = new OrderLineItemsDto(
                "Test order item", BigDecimal.valueOf(100), 1);
        List<OrderLineItemsDto> orderLine = new ArrayList<>();
        orderLine.add(orderLineItemsDto);
        return new OrderRequest(orderLine);
    }
}