package com.emma_ea.order_service.repository;

import com.emma_ea.order_service.dto.OrderLineItemsDto;
import com.emma_ea.order_service.dto.OrderRequest;
import com.emma_ea.order_service.model.Order;
import com.emma_ea.order_service.model.OrderLineItems;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Container
    @ClassRule
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("orders_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void shouldSaveOrder() {
        Order order = buildOrder(makeOrderRequest());

        Order savedOrder = orderRepository.save(order);

        Assertions.assertEquals(order.getId(), savedOrder.getId());
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

    private OrderRequest makeOrderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        OrderLineItemsDto items = new OrderLineItemsDto();
        items.setPrice(BigDecimal.valueOf(100));
        items.setSkuCode("Test Item");
        items.setQuantity(1);
        List<OrderLineItemsDto> list = new ArrayList<>();
        list.add(items);
        orderRequest.setOrderLineItemsDtoList(list);
        return orderRequest;
    }

}
