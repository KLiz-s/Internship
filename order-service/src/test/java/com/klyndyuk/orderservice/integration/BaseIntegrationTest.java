package com.klyndyuk.orderservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    static {
        postgres.start();
    }
}
