package com.emma_ea.order_service.service;

import com.emma_ea.order_service.dto.OrderLineItemsDto;
import com.emma_ea.order_service.dto.OrderRequest;
import com.emma_ea.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private final OrderRepository orderRepositoryMock = Mockito.mock(OrderRepository.class);

    private final OrderService orderServiceMock = Mockito.mock(
            OrderService.class, Mockito.withSettings().useConstructor(orderRepositoryMock));

    @Test
    @DisplayName("Create order should be verified if invoked")
    void createOrder() {

        orderServiceMock.createOrder(makeOrder());

        Mockito.verify(orderServiceMock, Mockito.times(1)).createOrder(makeOrder());

    }

    private OrderRequest makeOrder() {
        OrderLineItemsDto orderLineItemsDto = new OrderLineItemsDto(
                "Test order item", BigDecimal.valueOf(100), 1);
        List<OrderLineItemsDto> orderLine = new ArrayList<>();
        orderLine.add(orderLineItemsDto);
        return new OrderRequest(orderLine);
    }
}