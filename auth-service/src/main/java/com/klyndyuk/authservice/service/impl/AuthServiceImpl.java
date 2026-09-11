package com.klyndyuk.authservice.service.impl;

import com.klyndyuk.authservice.client.UserClient;
import com.klyndyuk.authservice.dto.request.CreateUserRequest;
import com.klyndyuk.authservice.dto.request.LoginRequest;
import com.klyndyuk.authservice.dto.request.RefreshRequest;
import com.klyndyuk.authservice.dto.request.RegistrationRequest;
import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.enums.Role;
import com.klyndyuk.authservice.exception.CredentialsNotFoundException;
import com.klyndyuk.authservice.exception.InvalidTokenException;
import com.klyndyuk.authservice.exception.LoginOccupiedException;
import com.klyndyuk.authservice.exception.RegistrationFailedException;
import com.klyndyuk.authservice.mapper.CredentialsMapper;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.security.entity.MyUserDetailsWithId;
import com.klyndyuk.authservice.security.entity.UserDetailsWithId;
import com.klyndyuk.authservice.security.service.JwtService;
import com.klyndyuk.authservice.security.service.UserDetailsWithIdService;
import com.klyndyuk.authservice.service.interfaces.AuthService;
import com.klyndyuk.authservice.service.interfaces.TwoTokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialsMapper credentialsMapper;
    private final TwoTokenService twoTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsWithIdService userDetailsService;
    private final UserClient userClient;
    public static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public void register(RegistrationRequest request) {
        if (credentialsRepository.existsByLogin(request.getLogin())) {
            throw new LoginOccupiedException(request.getLogin());
        }

        Credentials credentials = credentialsMapper.fromRegistrationRequest(request);

        credentials.setPassword(passwordEncoder.encode(request.getPassword()));

        credentials.setRole(Role.valueOf(DEFAULT_ROLE));

        credentialsRepository.saveAndFlush(credentials);

        try {
            CreateUserRequest createUserRequest = new CreateUserRequest();
            createUserRequest.setId(credentials.getUserId());
            createUserRequest.setName(request.getName());
            createUserRequest.setSurname(request.getSurname());
            createUserRequest.setBirthDate(request.getBirthDate());
            createUserRequest.setEmail(request.getEmail());
            userClient.registerUser(createUserRequest);
        } catch (Exception e) {
            credentialsRepository.deleteById(credentials.getUserId());
            throw new RegistrationFailedException();
        }
    }

    @Override
    public TwoTokenResponse login(LoginRequest loginRequest) throws NoSuchAlgorithmException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return twoTokenService.generateTokenPair(credentialsRepository.findByLogin(loginRequest.getLogin())
                    .orElseThrow(CredentialsNotFoundException::new));
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @Override
    public TwoTokenResponse refresh(RefreshRequest token) throws NoSuchAlgorithmException {
        return twoTokenService.updateTokenPair(token.getToken());
    }

    @Override
    public void logout(UserDetailsWithId user) {
        twoTokenService.logout(credentialsRepository.findById(UUID.fromString(user.getId())).orElseThrow(CredentialsNotFoundException::new));
    }

    @Override
    public void validateToken(String token) {
        try {
            String id = jwtService.extractUserId(token);
            UserDetailsWithId userDetails = userDetailsService.loadUserById(id);

            if (!jwtService.validateToken(token, userDetails)) {
                throw new InvalidTokenException();
            }
        } catch (JwtException |
                 UsernameNotFoundException |
                 IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }
}
