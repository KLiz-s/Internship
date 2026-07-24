package com.klyndyuk.userservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @ServiceConnection
    protected static final GenericContainer<?> redis =
            new GenericContainer<>("redis:8")
                    .withExposedPorts(6379);

    static {
        postgres.start();
        redis.start();
    }
}
