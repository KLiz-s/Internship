package com.klyndyuk.userservice.integration.controller;

import com.klyndyuk.userservice.integration.BaseDatabaseTest;
import com.klyndyuk.userservice.util.TestConstants;
import com.klyndyuk.userservice.util.TestJwtGenerator;
import com.klyndyuk.userservice.util.TestRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@AutoConfigureMockMvc
public abstract class BaseControllerTest
        extends BaseDatabaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private final TestJwtGenerator jwtGenerator = new TestJwtGenerator();

    protected String bearerToken(UUID userId, TestRole role) {
        return "Bearer " + jwtGenerator.generateToken(userId.toString(), role);
    }

    protected String adminBearerToken() {
        return bearerToken(TestConstants.USER_ID, TestRole.ROLE_ADMIN);
    }
}