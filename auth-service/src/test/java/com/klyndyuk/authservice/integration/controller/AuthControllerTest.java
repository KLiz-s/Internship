package com.klyndyuk.authservice.integration.controller;

import com.klyndyuk.authservice.dto.request.LoginRequest;
import com.klyndyuk.authservice.dto.request.RefreshRequest;
import com.klyndyuk.authservice.dto.request.RegistrationRequest;
import com.klyndyuk.authservice.dto.request.ValidateTokenRequest;
import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.entity.RefreshToken;
import com.klyndyuk.authservice.enums.Role;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.repository.RefreshTokenRepository;
import com.klyndyuk.authservice.utils.TestConstants;
import com.klyndyuk.authservice.utils.TestLoginRequests;
import com.klyndyuk.authservice.utils.TestRegistrationRequests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldRegister() throws Exception {
        RegistrationRequest request =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Credentials credentials = credentialsRepository
                .findByLogin(TestConstants.LOGIN)
                .orElseThrow();

        assertThat(credentials.getLogin())
                .isEqualTo(TestConstants.LOGIN);

        assertThat(credentials.getPassword())
                .isNotEqualTo(TestConstants.PASSWORD);

        assertThat(credentials.getRole())
                .isEqualTo(Role.ROLE_USER);
    }

    @Test
    void shouldReturnConflictWhenLoginAlreadyExists() throws Exception {
        RegistrationRequest request =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        assertThat(credentialsRepository.count())
                .isEqualTo(1);
    }

    @Test
    void shouldLogin() throws Exception {
        RegistrationRequest registrationRequest =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest =
                TestLoginRequests.createLoginRequest();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        assertThat(refreshTokenRepository.findAll())
                .hasSize(1);

        RefreshToken refreshToken =
                refreshTokenRepository.findAll().getFirst();

        assertThat(refreshToken.getRevokedAt()).isNull();
        assertThat(refreshToken.getCredentials().getLogin())
                .isEqualTo(TestConstants.LOGIN);
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsInvalid() throws Exception {
        RegistrationRequest registrationRequest =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        LoginRequest request = new LoginRequest();
        request.setLogin(TestConstants.LOGIN);
        request.setPassword("wrong-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertThat(refreshTokenRepository.findAll())
                .isEmpty();
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginDoesNotExist() throws Exception {
        LoginRequest request =
                TestLoginRequests.createLoginRequest();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertThat(refreshTokenRepository.findAll())
                .isEmpty();
    }

    @Test
    void shouldRefreshTokenPair() throws Exception {
        RegistrationRequest registrationRequest =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest =
                TestLoginRequests.createLoginRequest();

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TwoTokenResponse tokenResponse =
                objectMapper.readValue(loginResponse, TwoTokenResponse.class);

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setToken(tokenResponse.getRefreshToken());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        assertThat(refreshTokenRepository.findAll())
                .hasSize(2);

        RefreshToken oldToken = refreshTokenRepository.findAll()
                .stream()
                .filter(t -> t.getRevokedAt() != null)
                .findFirst()
                .orElseThrow();

        RefreshToken newToken = refreshTokenRepository.findAll()
                .stream()
                .filter(t -> t.getRevokedAt() == null)
                .findFirst()
                .orElseThrow();

        assertThat(oldToken.getReplacedBy().getId())
                .isEqualTo(newToken.getId());
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenIsInvalid() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setToken("invalid-token");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertThat(refreshTokenRepository.findAll())
                .isEmpty();
    }

    @Test
    void shouldLogout() throws Exception {
        RegistrationRequest registrationRequest =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest =
                TestLoginRequests.createLoginRequest();

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TwoTokenResponse tokens =
                objectMapper.readValue(response, TwoTokenResponse.class);

        assertThat(refreshTokenRepository.findAll())
                .hasSize(1);

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization",
                                "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());

        RefreshToken refreshToken =
                refreshTokenRepository.findAll()
                        .getFirst();

        assertThat(refreshToken.getRevokedAt())
                .isNotNull();
    }

    @Test
    void shouldNotRefreshAfterLogout() throws Exception {
        RegistrationRequest registrationRequest =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest =
                TestLoginRequests.createLoginRequest();

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TwoTokenResponse tokens =
                objectMapper.readValue(loginResponse, TwoTokenResponse.class);

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization",
                                "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setToken(tokens.getRefreshToken());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());

        assertThat(refreshTokenRepository.findAll())
                .allSatisfy(token ->
                        assertThat(token.getRevokedAt()).isNotNull());
    }

    @Test
    void shouldValidateToken() throws Exception {
        RegistrationRequest registrationRequest =
                TestRegistrationRequests.createRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest =
                TestLoginRequests.createLoginRequest();

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TwoTokenResponse tokenResponse =
                objectMapper.readValue(loginResponse, TwoTokenResponse.class);

        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken(tokenResponse.getAccessToken());

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken("invalid-token");

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenTokenIsBlank() throws Exception {
        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken("");

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}