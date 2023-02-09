package com.emma_ea.product;

import com.emma_ea.product.dto.ProductRequest;
import com.emma_ea.product.repository.ProductRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

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

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	void getShouldShouldObtainProductWithId() {

	}

	void getShouldObtainAllProducts() {

	}

	private ProductRequest makeProduct() {
		return ProductRequest.builder()
				.name("Oneplus 3")
				.description("Oneplus 3 android device")
				.price(BigDecimal.valueOf(1300))
				.build();
	}

}
