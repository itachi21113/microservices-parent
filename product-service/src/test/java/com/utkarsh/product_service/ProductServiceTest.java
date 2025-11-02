package com.utkarsh.product_service;

import com.utkarsh.product_service.dto.ProductRequest;
import com.utkarsh.product_service.dto.ProductResponse;
import com.utkarsh.product_service.model.Product;
import com.utkarsh.product_service.repository.ProductRepository;
import com.utkarsh.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void shouldCreateProduct() {
        ProductRequest productRequest = ProductRequest.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100))
                .build();

        productService.createProduct(productRequest);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository, times(1)).save(productArgumentCaptor.capture());

        Product savedProduct = productArgumentCaptor.getValue();

        assertEquals("Product 1", savedProduct.getName());
        assertEquals("Description 1", savedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(100), savedProduct.getPrice());



    }

    @Test
    public void shouldGetAllProducts() {

        List<Product> products = new ArrayList<>();
        products.add(Product.builder().id("1").name("Product 1").description("Description 1").price(BigDecimal.valueOf(100)).build());
        products.add(Product.builder().id("2").name("Product 2").description("Description 2").price(BigDecimal.valueOf(200)).build());

        when(productRepository.findAll()).thenReturn(products);

        List<ProductResponse> productResponses = productService.getAllProducts();

        assertEquals(2, productResponses.size());
        assertEquals("Product 1", productResponses.get(0).getName());
        assertEquals("Description 1", productResponses.get(0).getDescription());
        assertEquals(BigDecimal.valueOf(100), productResponses.get(0).getPrice());
        assertEquals("Product 2", productResponses.get(1).getName());
        assertEquals("Description 2", productResponses.get(1).getDescription());
        assertEquals(BigDecimal.valueOf(200), productResponses.get(1).getPrice());
    }
}
