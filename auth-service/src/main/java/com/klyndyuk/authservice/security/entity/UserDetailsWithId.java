package com.klyndyuk.authservice.security.entity;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsWithId extends UserDetails {
    String getId();
}
