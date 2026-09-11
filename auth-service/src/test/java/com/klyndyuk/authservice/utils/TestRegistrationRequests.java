package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.dto.request.RegistrationRequest;

public final class TestRegistrationRequests {

    private TestRegistrationRequests() {
    }

    public static RegistrationRequest createRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();

        request.setLogin(TestConstants.LOGIN);
        request.setPassword(TestConstants.PASSWORD);

        request.setName(TestConstants.USER_NAME);
        request.setSurname(TestConstants.USER_SURNAME);
        request.setEmail(TestConstants.USER_EMAIL);
        request.setBirthDate(TestConstants.USER_BIRTH_DATE);

        return request;
    }
}
