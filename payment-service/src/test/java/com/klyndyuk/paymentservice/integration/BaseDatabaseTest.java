package com.klyndyuk.paymentservice.integration;

import com.klyndyuk.paymentservice.entity.Payment;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public abstract class BaseDatabaseTest extends BaseIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void cleanDatabase() {
        mongoTemplate.dropCollection(Payment.class);
    }
}