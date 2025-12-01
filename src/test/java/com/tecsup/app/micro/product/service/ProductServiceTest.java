package com.tecsup.app.micro.product.service;

import com.tecsup.app.micro.product.client.User;
import com.tecsup.app.micro.product.client.UserClient;
import com.tecsup.app.micro.product.dto.Product;
import com.tecsup.app.micro.product.entity.ProductEntity;
import com.tecsup.app.micro.product.mapper.ProductMapper;
import com.tecsup.app.micro.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper mapper;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ProductService productService;

    private ProductEntity productEntity;
    private Product productDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        productEntity = new ProductEntity(1L, "Laptop", "Gamer", new BigDecimal("1500.00"), 10, "Computo", 10L);

        mockUser = User.builder()
                .id(10L)
                .name("Admin")
                .email("admin@test.com")
                .build();

        productDto = new Product(1L, "Laptop", "Gamer", new BigDecimal("1500.00"), 10, "Computo", mockUser);
    }

    @Test
    void getAllProducts() {
        List<ProductEntity> entities = Arrays.asList(productEntity);
        when(productRepository.findAll()).thenReturn(entities);
        when(mapper.toDomain(productEntity)).thenReturn(productDto);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void getProductByIdWithUser() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(userClient.getUserById(productEntity.getCreatedBy())).thenReturn(mockUser);
        when(mapper.toDomainWithUser(productEntity, mockUser)).thenReturn(productDto);

        Product result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals("Admin", result.getCreatedByUser().getName());

        verify(productRepository).findById(productId);
        verify(userClient).getUserById(10L);
    }

    @Test
    void save() {
        when(mapper.toEntity(productDto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(mapper.toDomain(productEntity)).thenReturn(productDto);

        Product result = productService.save(productDto);

        assertNotNull(result);
        assertEquals(productDto.getName(), result.getName());
        verify(productRepository).save(productEntity);
    }

    @Test
    void deleteById() {
        Long productId = 1L;
        productService.deleteById(productId);
        verify(productRepository).deleteById(productId);
    }
}