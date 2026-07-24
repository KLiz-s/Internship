package com.klyndyuk.userservice.integration.controller;

import com.klyndyuk.userservice.dto.request.CreateUserRequest;
import com.klyndyuk.userservice.dto.request.UpdateUserRequest;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.util.TestConstants;
import com.klyndyuk.userservice.util.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_shouldReturnCreated() throws Exception {
        CreateUserRequest request = TestUsers.createRequest();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(TestConstants.USER_NAME))
                .andExpect(jsonPath("$.surname").value(TestConstants.USER_SURNAME))
                .andExpect(jsonPath("$.email").value(TestConstants.USER_EMAIL))
                .andExpect(jsonPath("$.birthDate")
                        .value(TestConstants.USER_BIRTH_DATE.toString()))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(userRepository.count()).isEqualTo(1);

        User user = userRepository.findAll().getFirst();

        assertThat(user.getName()).isEqualTo(TestConstants.USER_NAME);
        assertThat(user.getSurname()).isEqualTo(TestConstants.USER_SURNAME);
        assertThat(user.getEmail()).isEqualTo(TestConstants.USER_EMAIL);
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void create_shouldReturnConflictWhenEmailOccupied() throws Exception {
        userRepository.save(TestUsers.createUser());

        CreateUserRequest request = TestUsers.createRequest();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void create_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        CreateUserRequest request = TestUsers.createRequest();
        request.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertThat(userRepository.count()).isZero();
    }
        @Test
    void getById_shouldReturnUser() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.name").value(TestConstants.USER_NAME))
                .andExpect(jsonPath("$.surname").value(TestConstants.USER_SURNAME))
                .andExpect(jsonPath("$.email").value(TestConstants.USER_EMAIL))
                .andExpect(jsonPath("$.birthDate")
                        .value(TestConstants.USER_BIRTH_DATE.toString()))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.paymentCards").isArray())
                .andExpect(jsonPath("$.paymentCards").isEmpty());
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());

        assertThat(userRepository.count()).isZero();
    }

    @Test
    void getAll_shouldReturnUsers() throws Exception {
        userRepository.save(TestUsers.createUser());
        userRepository.save(TestUsers.createSecondUser());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].surname").exists())
                .andExpect(jsonPath("$.content[0].email").exists())
                .andExpect(jsonPath("$.content[0].active").value(true));
    }

    @Test
    void getAll_shouldFilterByName() throws Exception {
        userRepository.save(TestUsers.createUser());
        userRepository.save(TestUsers.createSecondUser());

        mockMvc.perform(get("/users")
                        .param("name", TestConstants.USER_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value(TestConstants.USER_NAME));
    }
    
    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        UpdateUserRequest request = TestUsers.createUpdateRequest();

        mockMvc.perform(put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.surname").value(request.getSurname()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.birthDate")
                        .value(request.getBirthDate().toString()))
                .andExpect(jsonPath("$.active").value(true));

        User updated = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo(request.getName());
        assertThat(updated.getSurname()).isEqualTo(request.getSurname());
        assertThat(updated.getEmail()).isEqualTo(request.getEmail());
        assertThat(updated.getBirthDate()).isEqualTo(request.getBirthDate());
    }

    @Test
    void update_shouldReturnConflictWhenEmailOccupied() throws Exception {
        User first = userRepository.save(TestUsers.createUser());
        User second = userRepository.save(TestUsers.createSecondUser());

        UpdateUserRequest request = TestUsers.createUpdateRequest();
        request.setEmail(first.getEmail());

        mockMvc.perform(put("/users/{id}", second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void update_shouldReturnNotFound() throws Exception {
        UpdateUserRequest request = TestUsers.createUpdateRequest();

        mockMvc.perform(put("/users/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());

        assertThat(userRepository.count()).isZero();
    }

    @Test
    void activate_shouldUpdateUserStatus() throws Exception {
        User user = TestUsers.createUser();
        user.setActive(false);

        user = userRepository.save(user);

        mockMvc.perform(patch("/users/{id}/activate", user.getId()))
                .andExpect(status().isOk());

        User updated = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updated.isActive()).isTrue();
    }

    @Test
    void deactivate_shouldUpdateUserStatus() throws Exception {
        User user = userRepository.save(TestUsers.createUser());

        mockMvc.perform(patch("/users/{id}/deactivate", user.getId()))
                .andExpect(status().isOk());

        User updated = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updated.isActive()).isFalse();
    }

    @Test
    void activate_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/users/{id}/activate", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deactivate_shouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/users/{id}/deactivate", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}