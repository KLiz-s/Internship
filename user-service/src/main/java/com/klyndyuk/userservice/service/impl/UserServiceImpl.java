package com.klyndyuk.userservice.service.impl;

import com.klyndyuk.userservice.dto.request.CreateUserRequest;
import com.klyndyuk.userservice.dto.request.UpdateUserRequest;
import com.klyndyuk.userservice.dto.response.UserDetailsResponse;
import com.klyndyuk.userservice.dto.response.UserResponse;
import com.klyndyuk.userservice.entity.User;
import com.klyndyuk.userservice.exception.EmailOccupiedException;
import com.klyndyuk.userservice.exception.UserNotFoundException;
import com.klyndyuk.userservice.mapper.UserMapper;
import com.klyndyuk.userservice.repository.UserRepository;
import com.klyndyuk.userservice.service.interfaces.UserService;
import com.klyndyuk.userservice.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse create(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailOccupiedException(user.getEmail());
        }
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }


    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDetailsResponse getById(UUID id) {
        User user = userRepository.findWithPaymentCardsById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDetailsResponse(user);
    }

    @Override
    public Page<UserResponse> getAll(String name,
                                     String surname,
                                     Pageable pageable) {
        Specification<User> specification =
                UserSpecification.byFilters(name, surname);

        return userRepository.findAll(specification, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserResponse update(UUID id,
                               UpdateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailOccupiedException(request.getEmail());
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateEntity(request, user);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void updateActive(UUID id, boolean active) {
        int updated = userRepository.updateActive(id, active);

        if (updated == 0) {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}