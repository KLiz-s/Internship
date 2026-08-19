package com.klyndyuk.userservice.controller;

import com.klyndyuk.userservice.dto.request.CreateUserRequest;
import com.klyndyuk.userservice.dto.request.UpdateUserRequest;
import com.klyndyuk.userservice.dto.response.UserDetailsResponse;
import com.klyndyuk.userservice.dto.response.UserResponse;
import com.klyndyuk.userservice.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsResponse> getUserById(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        UserDetailsResponse response = userService.getById(id, userDetails);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable) {

        Page<UserResponse> response = userService.getAll(
                name,
                surname,
                pageable
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request, @AuthenticationPrincipal UserDetails userDetails) {

        UserResponse response = userService.update(id, request, userDetails);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {

        userService.updateActive(id, true, userDetails);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {

        userService.updateActive(id, false,  userDetails);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {

        userService.delete(id, userDetails);

        return ResponseEntity.noContent().build();
    }
}