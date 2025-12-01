package com.tecsup.app.micro.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.app.micro.product.client.User;
import com.tecsup.app.micro.product.client.UserClient;
import com.tecsup.app.micro.product.entity.ProductEntity;
import com.tecsup.app.micro.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class ProductControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private UserClient userClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getProductById_WithUserIntegration() throws Exception {
        Long USER_ID = 10L;
        ProductEntity saved = productRepository.save(new ProductEntity(null, "Laptop Gamer", "Potente", new BigDecimal("2500.00"), 5, "Computo", USER_ID));

        User mockUser = User.builder()
                .id(USER_ID)
                .name("Admin Tecsup")
                .email("admin@tecsup.edu.pe")
                .build();

        given(userClient.getUserById(USER_ID)).willReturn(mockUser);

        this.mockMvc.perform(get("/api/products/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Laptop Gamer")))
                .andExpect(jsonPath("$.createdByUser.name", is("Admin Tecsup")));
    }

    @Test
    void getAllProducts() throws Exception {
        productRepository.save(new ProductEntity(null, "Mouse", "Mouse óptico", new BigDecimal("25.50"), 100, "Accesorios", 1L));

        this.mockMvc.perform(get("/api/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        String NAME = "Teclado Mecánico";
        Double PRICE = 150.00;

        Map<String, Object> body = new HashMap<>();
        body.put("name", NAME);
        body.put("description", "RGB Lights");
        body.put("price", PRICE);
        body.put("stock", 20);
        body.put("category", "Periféricos");

        this.mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.price", is(PRICE)));
    }

    @Test
    @Transactional
    void updateProduct() throws Exception {
        ProductEntity saved = productRepository.save(new ProductEntity(null, "Monitor", "LED", new BigDecimal("400.00"), 10, "Video", 1L));

        String UPDATED_NAME = "Monitor IPS";
        Map<String, Object> body = new HashMap<>();
        body.put("name", UPDATED_NAME);
        body.put("description", "LED Panel IPS");
        body.put("price", 450.00);
        body.put("stock", 10);
        body.put("category", "Video");

        this.mockMvc.perform(put("/api/products/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(UPDATED_NAME)));
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        ProductEntity saved = productRepository.save(new ProductEntity(null, "Cable HDMI", "2m", new BigDecimal("10.00"), 50, "Cables", 1L));

        this.mockMvc.perform(delete("/api/products/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}