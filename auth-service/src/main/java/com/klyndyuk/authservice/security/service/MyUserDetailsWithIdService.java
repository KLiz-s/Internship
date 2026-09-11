package com.klyndyuk.authservice.security.service;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.security.entity.MyUserDetailsWithId;
import com.klyndyuk.authservice.security.entity.UserDetailsWithId;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@NullMarked
public class MyUserDetailsWithIdService implements UserDetailsWithIdService{
    private final CredentialsRepository credentialsRepository;

    public MyUserDetailsWithIdService(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    public UserDetailsWithId loadUserByUsername(String username) {
        Credentials credentials = credentialsRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new MyUserDetailsWithId(credentials);
    }

    @Override
    public UserDetailsWithId loadUserById(String id) {
        Credentials credentials = credentialsRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UsernameNotFoundException(id));
        return new MyUserDetailsWithId(credentials);
    }
}

