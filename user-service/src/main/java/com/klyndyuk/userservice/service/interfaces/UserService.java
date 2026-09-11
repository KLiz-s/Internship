package com.klyndyuk.userservice.service.interfaces;

import com.klyndyuk.userservice.dto.request.CreateUserRequest;
import com.klyndyuk.userservice.dto.request.UpdateUserRequest;
import com.klyndyuk.userservice.dto.response.UserDetailsResponse;
import com.klyndyuk.userservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserDetailsResponse getById(UUID id, UserDetails userDetails);

    Page<UserResponse> getAll(String name,
                              String surname,
                              Pageable pageable);

    UserResponse update(UUID id, UpdateUserRequest request, UserDetails userDetails);

    void updateActive(UUID id, boolean active, UserDetails userDetails);

    void delete(UUID id, UserDetails userDetails);
}