package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.dto.request.CreateUserRequest;
import com.klyndyuk.authservice.dto.request.RegistrationRequest;

public class TestCreateUserRequests {
    private TestCreateUserRequests() {
    }

    public static CreateUserRequest createCreateUserRequest() {
        RegistrationRequest registrationRequest = TestRegistrationRequests.createRegistrationRequest();
        CreateUserRequest request = new CreateUserRequest();
        request.setName(registrationRequest.getName());
        request.setSurname(registrationRequest.getSurname());
        request.setEmail(registrationRequest.getEmail());
        request.setBirthDate(registrationRequest.getBirthDate());
        return request;
    }
}
