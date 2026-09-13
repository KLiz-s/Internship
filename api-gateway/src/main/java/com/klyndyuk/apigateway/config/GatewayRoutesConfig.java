package com.klyndyuk.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(
            RouteLocatorBuilder builder,
              @Value("${user-service.url}") String userServiceUrl,
              @Value("${auth-service.url}") String authServiceUrl,
              @Value("${order-service.url}") String orderServiceUrl,
              @Value("${payment-service.url}") String paymentServiceUrl) {
                
        return builder
                .routes()

                .route("user-service", route -> route
                        .path("/api/users/**")
                        .uri(userServiceUrl)
                )

                .route("user-service-cards", route -> route
                        .path("/api/cards/**")
                        .uri(userServiceUrl)
                )

                .route("auth-service", route -> route
                        .path("/api/auth/**")
                        .uri(authServiceUrl)
                )

                .route("order-service", route -> route
                        .path("/api/orders/**")
                        .uri(orderServiceUrl)
                )

                .route("payment-service", route -> route
                        .path("/api/payments/**")
                        .uri(paymentServiceUrl)
                )

                .build();
    }
}