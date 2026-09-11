package com.klyndyuk.authservice.security.entity;

import com.klyndyuk.authservice.entity.Credentials;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NullMarked
public class MyUserDetailsWithId implements UserDetailsWithId {
    private final UUID id;
    private final String password;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public MyUserDetailsWithId(Credentials credentials) {
        this.id = credentials.getUserId();
        this.password = credentials.getPassword();
        this.username = credentials.getLogin();
        this.authorities = List.of(new SimpleGrantedAuthority(credentials.getRole().toString()));
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}

