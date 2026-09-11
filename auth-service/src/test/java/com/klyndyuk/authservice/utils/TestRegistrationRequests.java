package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.dto.request.RegistrationRequest;

public final class TestRegistrationRequests {

    private TestRegistrationRequests() {
    }

    public static RegistrationRequest createRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();

        request.setLogin(TestConstants.LOGIN);
        request.setPassword(TestConstants.PASSWORD);

        return request;
    }
}
