package com.klyndyuk.userservice.integration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDatabaseTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM payment_cards");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @AfterEach
    void cleanRedis() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            connection.serverCommands().flushAll();
        }
    }
}