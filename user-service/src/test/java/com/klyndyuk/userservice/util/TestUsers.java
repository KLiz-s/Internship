package com.klyndyuk.userservice.util;

import com.klyndyuk.userservice.dto.request.CreateUserRequest;
import com.klyndyuk.userservice.dto.request.UpdateUserRequest;
import com.klyndyuk.userservice.dto.response.UserDetailsResponse;
import com.klyndyuk.userservice.dto.response.UserResponse;
import com.klyndyuk.userservice.entity.User;

import java.util.Locale;
import java.util.UUID;

public final class TestUsers {

    private TestUsers() {
    }

    public static User createUser() {
        User user = new User();
        user.setId(TestConstants.USER_ID);
        user.setName(TestConstants.USER_NAME);
        user.setSurname(TestConstants.USER_SURNAME);
        user.setEmail(TestConstants.USER_EMAIL);
        user.setBirthDate(TestConstants.USER_BIRTH_DATE);
        user.setActive(true);

        return user;
    }

    public static User createUser(UUID id) {
        User user = createUser();
        user.setId(id);
        return user;
    }

    public static User createUser(UUID id, String email) {
        User user = createUser(id);
        user.setEmail(email);
        return user;
    }

    public static User createSecondUser() {
        User user = new User();
        user.setId(TestConstants.USER_ID_2);
        user.setName(TestConstants.USER_NAME_2);
        user.setSurname(TestConstants.USER_SURNAME_2);
        user.setEmail(TestConstants.USER_EMAIL_2);
        user.setBirthDate(TestConstants.USER_BIRTH_DATE_2);
        user.setActive(true);

        return user;
    }

    public static CreateUserRequest createRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setId(TestConstants.USER_ID);
        request.setName(TestConstants.USER_NAME);
        request.setSurname(TestConstants.USER_SURNAME);
        request.setEmail(TestConstants.USER_EMAIL);
        request.setBirthDate(TestConstants.USER_BIRTH_DATE);

        return request;
    }

    public static UpdateUserRequest createUpdateRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName(TestConstants.UPDATED_USER_NAME);
        request.setSurname(TestConstants.UPDATED_USER_SURNAME);
        request.setBirthDate(TestConstants.USER_BIRTH_DATE);
        request.setEmail(TestConstants.UPDATED_USER_EMAIL);

        return request;
    }

    public static UserResponse createUserResponse() {
        UserResponse response = new UserResponse();
        response.setId(TestConstants.USER_ID);
        response.setName(TestConstants.USER_NAME);
        response.setSurname(TestConstants.USER_SURNAME);
        response.setEmail(TestConstants.USER_EMAIL);
        response.setBirthDate(TestConstants.USER_BIRTH_DATE);
        response.setActive(true);

        return response;
    }

    public static UserResponse createUserResponse(UUID id) {
        UserResponse response = createUserResponse();
        response.setId(id);
        return response;
    }

    public static UserDetailsResponse createDetailsResponse() {
        UserDetailsResponse response = new UserDetailsResponse();
        response.setId(TestConstants.USER_ID);
        response.setName(TestConstants.USER_NAME);
        response.setSurname(TestConstants.USER_SURNAME);
        response.setEmail(TestConstants.USER_EMAIL);
        response.setBirthDate(TestConstants.USER_BIRTH_DATE);
        response.setActive(true);

        return response;
    }

    public static UserDetailsResponse createDetailsResponse(UUID id) {
        UserDetailsResponse response = createDetailsResponse();
        response.setId(id);
        return response;
    }

    public static User createUser(String name) {
        User user = createUser();
        user.setName(name);
        user.setEmail(email(name));
        return user;
    }

    public static User createUser(String name, String surname) {
        User user = createUser(name);
        user.setSurname(surname);
        user.setEmail(email(name, surname));
        return user;
    }

    public static User createUser(String name,
                                  String surname,
                                  String email) {
        User user = createUser(name, surname);
        user.setEmail(email);
        return user;
    }

    public static User createUser(UUID id,
                                  String name,
                                  String surname) {
        User user = createUser(id);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email(name, surname));
        return user;
    }

    public static User createUser(UUID id,
                                  String name,
                                  String surname,
                                  String email) {
        User user = createUser(id, name, surname);
        user.setEmail(email);
        return user;
    }

    private static String email(String... parts) {
        return String.join(".", parts)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9.]", "") + "@example.com";
    }
}
