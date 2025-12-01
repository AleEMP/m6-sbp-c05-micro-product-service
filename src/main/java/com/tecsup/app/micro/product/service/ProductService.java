package com.tecsup.app.micro.product.service;

import com.tecsup.app.micro.product.client.User;
import com.tecsup.app.micro.product.client.UserClient;
import com.tecsup.app.micro.product.dto.Product;
import com.tecsup.app.micro.product.entity.ProductEntity;
import com.tecsup.app.micro.product.mapper.ProductMapper;
import com.tecsup.app.micro.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final UserClient userClient;

    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public Product getProductById(Long id){
        // 1. Obtener producto de la base de datos local
        ProductEntity productEntity = productRepository.findById(id).orElse(null);

        if(productEntity == null) return null;

        // 2. Comunicarse con el microservicio de User para obtener detalles del creador
        User user = null;
        if (productEntity.getCreatedBy() != null) {
            user = userClient.getUserById(productEntity.getCreatedBy());
            log.info(" User retrieved: {}", user);
        }

        // 3. Combinar producto y usuario en el DTO
        return mapper.toDomainWithUser(productEntity, user);
    }

    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = productRepository.save(entity);
        return mapper.toDomain(saved);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}