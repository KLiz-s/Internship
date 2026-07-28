package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.dto.request.LoginRequest;

public final class TestLoginRequests {

    private TestLoginRequests() {
    }

    public static LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();

        request.setLogin(TestConstants.LOGIN);
        request.setPassword(TestConstants.PASSWORD);

        return request;
    }
}