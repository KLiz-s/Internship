package com.klyndyuk.paymentservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.mongodb.MongoDBContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @ServiceConnection
    protected static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:8");

    static {
        mongo.start();
    }
}
