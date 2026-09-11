package com.klyndyuk.authservice.client;

import com.klyndyuk.authservice.dto.request.CreateUserRequest;
import com.klyndyuk.authservice.dto.response.UserResponse;
import com.klyndyuk.authservice.exception.ConnectionFailedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClient webClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "registerUserFallback")
    public UserResponse registerUser(CreateUserRequest createUserRequest) {
        return webClient.post()
                .uri("/api/users")
                .bodyValue(createUserRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
    }

    public UserResponse registerUserFallback(CreateUserRequest createUserRequest, Throwable throwable) {
        throw new ConnectionFailedException("User service is unavailable. Please try again later or try other email.");
    }
}
