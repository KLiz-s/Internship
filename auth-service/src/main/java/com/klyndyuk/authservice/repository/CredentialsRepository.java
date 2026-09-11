package com.klyndyuk.authservice.repository;

import com.klyndyuk.authservice.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
    Optional<Credentials> findByLogin(String login);

    boolean existsByLogin(String login);
}
