package com.klyndyuk.orderservice.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient userServiceClient(@Value("${user-service.url}") String userServiceUrl) {
        return WebClient.builder().baseUrl(userServiceUrl)
                .filter((request, next) -> {
                    ServletRequestAttributes attributes =
                            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                    if (attributes == null) {
                        return next.exchange(request);
                    }

                    HttpServletRequest currentRequest = attributes.getRequest();

                    String authorization =
                            currentRequest.getHeader(HttpHeaders.AUTHORIZATION);

                    if (authorization == null) {
                        return next.exchange(request);
                    }

                    ClientRequest newRequest = ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, authorization)
                            .build();

                    return next.exchange(newRequest);
                })
                .build();
    }
}
