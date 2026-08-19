package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.dto.request.CreateUserRequest;
import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.dto.response.UserResponse;

public final class TestResponses {

    private TestResponses() {
    }

    public static TwoTokenResponse createTwoTokenResponse() {
        return new TwoTokenResponse(
                TestConstants.ACCESS_TOKEN,
                TestConstants.REFRESH_TOKEN
        );
    }

    public static UserResponse createUserResponse() {
        CreateUserRequest request = TestCreateUserRequests.createCreateUserRequest();
        UserResponse response = new UserResponse();
        response.setId(TestConstants.USER_ID);
        response.setName(request.getName());
        response.setSurname(request.getSurname());
        response.setEmail(request.getEmail());
        response.setBirthDate(request.getBirthDate());
        response.setActive(true);
        return response;
    }
}
