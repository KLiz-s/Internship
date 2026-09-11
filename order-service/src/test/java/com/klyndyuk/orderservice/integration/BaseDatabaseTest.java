package com.klyndyuk.orderservice.integration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDatabaseTest extends BaseIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM order_items");
        jdbcTemplate.execute("DELETE FROM orders");
        jdbcTemplate.execute("DELETE FROM items");
    }
}
