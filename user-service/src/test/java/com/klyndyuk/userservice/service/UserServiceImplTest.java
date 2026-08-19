package com.klyndyuk.userservice.service;

import com.klyndyuk.userservice.dto.request.CreateUserRequest;
import com.klyndyuk.userservice.dto.request.UpdateUserRequest;
import com.klyndyuk.userservice.dto.response.UserDetailsResponse;
import com.klyndyuk.userservice.dto.response.UserResponse;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.exception.EmailOccupiedException;
import com.klyndyuk.userservice.exception.UserNotFoundException;
import com.klyndyuk.userservice.mapper.UserMapper;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.service.impl.UserServiceImpl;
import com.klyndyuk.userservice.util.TestConstants;
import com.klyndyuk.userservice.util.TestRole;
import com.klyndyuk.userservice.util.TestUsers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_shouldReturnCreatedUser() {
        CreateUserRequest request = TestUsers.createRequest();
        User user = TestUsers.createUser();
        UserResponse response = TestUsers.createUserResponse();

        given(userMapper.toEntity(request))
                .willReturn(user);

        given(userRepository.save(user))
                .willReturn(user);

        given(userMapper.toResponse(user))
                .willReturn(response);

        given(userRepository.existsByEmail(TestConstants.USER_EMAIL))
                .willReturn(false);

        UserResponse result = userService.create(request);

        assertThat(result).isSameAs(response);

        then(userMapper).should().toEntity(request);
        then(userRepository).should().save(user);
        then(userMapper).should().toResponse(user);
    }

    @Test
    void create_shouldThrowWhenEmailOccupied() {
        CreateUserRequest request = TestUsers.createRequest();
        User user = TestUsers.createUser();

        given(userMapper.toEntity(request))
                .willReturn(user);

        given(userRepository.existsByEmail(user.getEmail()))
                .willReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(EmailOccupiedException.class);

        then(userRepository).should().existsByEmail(user.getEmail());
        then(userRepository).should(never()).save(any());
        then(userMapper).should(never()).toResponse(any());
    }

    @Test
    void getById_shouldReturnUserDetails() {
        User user = TestUsers.createUser(TestConstants.USER_ID);
        UserDetailsResponse response = TestUsers.createDetailsResponse();

        given(userRepository.findWithPaymentCardsById(TestConstants.USER_ID))
                .willReturn(Optional.of(user));

        given(userMapper.toDetailsResponse(user))
                .willReturn(response);

        UserDetails userDetails = userDetails(TestConstants.USER_ID, TestRole.ROLE_USER);

        UserDetailsResponse result = userService.getById(TestConstants.USER_ID, userDetails);

        assertThat(result).isSameAs(response);

        then(userRepository)
                .should()
                .findWithPaymentCardsById(TestConstants.USER_ID);

        then(userMapper)
                .should()
                .toDetailsResponse(user);
    }

    @Test
    void getById_shouldThrowWhenUserNotFound() {
        given(userRepository.findWithPaymentCardsById(TestConstants.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(
                TestConstants.USER_ID,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        ))
                .isInstanceOf(UserNotFoundException.class);

        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    void getAll_shouldReturnUsersPage() {
        User user = TestUsers.createUser();
        UserResponse response = TestUsers.createUserResponse();

        Page<User> users = new PageImpl<>(List.of(user));
        PageRequest pageable = PageRequest.of(0, 10);

        given(userRepository.findAll(any(Specification.class), eq(pageable)))
                .willReturn(users);

        given(userMapper.toResponse(user))
                .willReturn(response);

        Page<UserResponse> result =
                userService.getAll(
                        TestConstants.USER_NAME,
                        TestConstants.USER_SURNAME,
                        pageable
                );

        assertThat(result.getContent())
                .containsExactly(response);

        then(userRepository)
                .should()
                .findAll(any(Specification.class), eq(pageable));

        then(userMapper)
                .should()
                .toResponse(user);
    }

    @Test
    void update_shouldReturnUpdatedUser() {
        UpdateUserRequest request = TestUsers.createUpdateRequest();
        User user = TestUsers.createUser(TestConstants.USER_ID);
        UserResponse response = TestUsers.createUserResponse();

        given(userRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.of(user));

        given(userRepository.save(user))
                .willReturn(user);

        given(userMapper.toResponse(user))
                .willReturn(response);

        given(userRepository.existsByEmail(request.getEmail()))
                .willReturn(false);

        UserResponse result =
                userService.update(
                        TestConstants.USER_ID,
                        request,
                        userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
                );

        assertThat(result).isSameAs(response);

        then(userRepository)
                .should()
                .findById(TestConstants.USER_ID);

        then(userMapper)
                .should()
                .updateEntity(request, user);

        then(userRepository)
                .should()
                .save(user);

        then(userMapper)
                .should()
                .toResponse(user);
    }

    @Test
    void update_shouldThrowWhenEmailOccupied() {
        UpdateUserRequest request = TestUsers.createUpdateRequest();

        given(userRepository.existsByEmail(request.getEmail()))
                .willReturn(true);

        assertThatThrownBy(() ->
                userService.update(
                        TestConstants.USER_ID,
                        request,
                        userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
                ))
                .isInstanceOf(EmailOccupiedException.class);

        then(userRepository).should().existsByEmail(request.getEmail());
        then(userRepository).should(never()).findById(any());
        then(userRepository).should(never()).save(any());
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        given(userRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.update(
                        TestConstants.USER_ID,
                        TestUsers.createUpdateRequest(),
                        userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
                )
        ).isInstanceOf(UserNotFoundException.class);

        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    void updateActive_shouldUpdateUserStatus() {
        given(userRepository.getReferenceById(TestConstants.USER_ID))
                .willReturn(TestUsers.createUser(TestConstants.USER_ID));

        given(userRepository.updateActive(TestConstants.USER_ID, true))
                .willReturn(1);

        userService.updateActive(
                TestConstants.USER_ID,
                true,
                userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
        );

        then(userRepository)
                .should()
                .updateActive(TestConstants.USER_ID, true);
    }

    @Test
    void updateActive_shouldThrowWhenUserNotFound() {
        given(userRepository.getReferenceById(TestConstants.USER_ID))
                .willReturn(TestUsers.createUser(TestConstants.USER_ID));

        given(userRepository.updateActive(TestConstants.USER_ID, false))
                .willReturn(0);

        assertThatThrownBy(() ->
                userService.updateActive(
                        TestConstants.USER_ID,
                        false,
                        userDetails(TestConstants.USER_ID, TestRole.ROLE_USER)
                )
        ).isInstanceOf(UserNotFoundException.class);

        then(userRepository)
                .should()
                .updateActive(TestConstants.USER_ID, false);
    }

    private UserDetails userDetails(UUID userId, TestRole role) {
        UserDetails userDetails = mock(UserDetails.class);
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(role.name());

        lenient().when(userDetails.getUsername()).thenReturn(userId.toString());
        lenient().doReturn(List.of(authority)).when(userDetails).getAuthorities();

        return userDetails;
    }
}
