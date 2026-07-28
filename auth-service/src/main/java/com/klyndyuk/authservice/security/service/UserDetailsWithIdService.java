package com.klyndyuk.authservice.security.service;

import com.klyndyuk.authservice.security.entity.UserDetailsWithId;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;

@NullMarked
public interface UserDetailsWithIdService extends UserDetailsService {
    @Override
    UserDetailsWithId loadUserByUsername(String username);

    UserDetailsWithId loadUserById(String id);
}
