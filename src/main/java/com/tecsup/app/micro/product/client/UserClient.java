package com.tecsup.app.micro.product.client;

// 1. Eliminamos el import de resilience4j
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    // 2. Quitamos la anotación @CircuitBreaker
    public User getUserById(Long createdBy) {
        String url = userServiceUrl + "/api/users/" + createdBy;

        // 3. Usamos un try-catch simple para replicar el "fallback"
        try {
            User usr = restTemplate.getForObject(url, User.class);
            log.info("User retrieved successfully from userdb: {}", usr);
            return usr;
        } catch (Exception e) {
            // 4. Aquí llamamos a la lógica de error si falla la conexión
            return getUserByIdFallback(createdBy, e);
        }
    }

    // Convertimos este método en privado y lo llamamos manualmente desde el catch
    private User getUserByIdFallback(Long createdBy, Throwable throwable) {
        log.warn("Error calling User Service (Fallback active): {}", throwable.getMessage());
        return User.builder()
                .id(createdBy)
                .name("Unknown User")
                .email("Unknown Email")
                .phone("Unknown Phone")
                .address("Unknown Address")
                .build();
    }
}