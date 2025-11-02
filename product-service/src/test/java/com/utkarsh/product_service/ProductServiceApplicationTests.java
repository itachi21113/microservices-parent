package com.utkarsh.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utkarsh.product_service.dto.ProductRequest;
import com.utkarsh.product_service.model.Product;
import com.utkarsh.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

	@Autowired
	ProductRepository productRepository;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}
	@BeforeEach
	void cleanup() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		// Arrange
		ProductRequest productRequest = ProductRequest.builder()
				.name("Integration Test Phone")
				.description("A phone from an integration test")
				.price(new BigDecimal("1200"))
				.build();
		String productRequestString = objectMapper.writeValueAsString(productRequest);

		// Act
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString))
				// Assert (Part 1: Check the HTTP Response)
				.andExpect(status().isCreated());

		// Assert (Part 2: Check the Database)
		Assertions.assertEquals(1, productRepository.findAll().size());
		Assertions.assertEquals("Integration Test Phone", productRepository.findAll().get(0).getName());
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		// Arrange
		Product testProduct = Product.builder()
				.name("Test Product for GET")
				.description("A test")
				.price(BigDecimal.TEN)
				.build();
		productRepository.save(testProduct);

		// Act & Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(status().isOk())
				.andExpect(result -> {
					String jsonResponse = result.getResponse().getContentAsString();
					// Assert that the JSON response contains our product's name
					Assertions.assertTrue(jsonResponse.contains("Test Product for GET"));
				});
	}

}
