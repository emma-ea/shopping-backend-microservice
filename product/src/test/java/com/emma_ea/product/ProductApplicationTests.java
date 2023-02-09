package com.emma_ea.product;

import com.emma_ea.product.dto.ProductRequest;
import com.emma_ea.product.dto.ProductResponse;
import com.emma_ea.product.model.Product;
import com.emma_ea.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductApplicationTests {

	@Container
	static final MongoDBContainer mongoDBContainer =
			new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setUriProperties(DynamicPropertyRegistry  dymProp) {
		dymProp.add("spring.data.mongo.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Before
	public void setup() {
		mongoDBContainer.start();
	}

	@Test
	void postShouldCreateProduct() throws Exception {
		String productRequest = objectMapper.writeValueAsString(makeProduct());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequest))
				.andExpect(status().isCreated());
	}

	@Test
	void getShouldReturnProductWithId() throws Exception {
		List<Product> products = productRepository.findAll();

		String firstId = products.stream().findFirst().get().getId();

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/product/{id}", firstId))
				.andExpect(status().isOk())
				.andReturn();

		ProductResponse rep = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);

		Assertions.assertEquals(firstId, rep.getId());
	}

	@Test
	void getShouldReturnAllProducts() throws Exception {
		MvcResult reqResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(status().isOk())
				.andReturn();

		String response = reqResult.getResponse().getContentAsString();
		List<ProductResponse> products = Arrays.asList(objectMapper.readValue(response, ProductResponse[].class));

		Assertions.assertTrue(products.size() > 1);
	}

	private ProductRequest makeProduct() {
		return ProductRequest.builder()
				.name("Test product name")
				.description("Test product description")
				.price(BigDecimal.valueOf(1300000))
				.build();
	}

}
