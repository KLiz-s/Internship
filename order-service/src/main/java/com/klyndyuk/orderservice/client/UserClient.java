package com.klyndyuk.orderservice.client;

import com.klyndyuk.orderservice.dto.response.UserResponse;
import com.klyndyuk.orderservice.exception.ConnectionFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient webClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    public UserResponse getUser(String id) {
        return webClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
    }

    public UserResponse getUserFallback(String id, Throwable throwable) {
        throw new ConnectionFailedException("User service is unavailable. Please try again later.");
    }
}
