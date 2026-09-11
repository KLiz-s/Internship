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
            @Value("${services.address.prefix}") String servicesAddressPrefix,
            @Value("${user-service.host}") String userServiceHost,
            @Value("${auth-service.host}") String authServiceHost,
            @Value("${order-service.host}") String orderServiceHost,
            @Value("${payment-service.host}") String paymentServiceHost,
            @Value("${user-service.port}") String userServicePort,
            @Value("${auth-service.port}") String authServicePort,
            @Value("${order-service.port}") String orderServicePort,
            @Value("${payment-service.port}") String paymentServicePort) {

        return builder
                .routes()

                .route("user-service", route -> route
                        .path("/api/users/**")
                        .uri(servicesAddressPrefix + userServiceHost + ":" + userServicePort)
                )

                .route("user-service-cards", route -> route
                        .path("/api/cards/**")
                        .uri(servicesAddressPrefix + userServiceHost + ":" + userServicePort)
                )

                .route("auth-service", route -> route
                        .path("/api/auth/**")
                        .uri(servicesAddressPrefix + authServiceHost + ":" + authServicePort)
                )

                .route("order-service", route -> route
                        .path("/api/orders/**")
                        .uri(servicesAddressPrefix + orderServiceHost + ":" + orderServicePort)
                )

                .route("payment-service", route -> route
                        .path("/api/payments/**")
                        .uri(servicesAddressPrefix + paymentServiceHost + ":" + paymentServicePort)
                )

                .build();
    }
}