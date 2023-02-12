package com.emma_ea.order_service;

import com.emma_ea.order_service.dto.OrderLineItemsDto;
import com.emma_ea.order_service.dto.OrderRequest;
import com.emma_ea.order_service.model.OrderLineItems;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderServiceApplicationTests {

	@Container
	@ClassRule
	private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("order_db")
			.withUsername("postgres")
			.withPassword("postgres");

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dym) {
		dym.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
	}

	@Test
	void postShouldCreateOrderAndReturnSuccessfulCreatedResponse() throws Exception {

		String orderRequest = objectMapper.writeValueAsString(makeOrderRequest());

		MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.post(("/api/order"))
				.content(orderRequest)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		String response = result.getResponse().getContentAsString();
		Assertions.assertTrue(response.toLowerCase().contains("successful"));
	}

	private OrderRequest makeOrderRequest() {
		OrderRequest orderRequest = new OrderRequest();
		OrderLineItemsDto items = new OrderLineItemsDto();
		items.setPrice(BigDecimal.valueOf(1100));
		items.setSkuCode("ITest 1");
		items.setQuantity(2);
		List<OrderLineItemsDto> list = new ArrayList<>();
		list.add(items);
		orderRequest.setOrderLineItemsDtoList(list);
		return orderRequest;
	}

}
