package com.klyndyuk.authservice.repository;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE RefreshToken t
        SET t.revokedAt = CURRENT_TIMESTAMP
        WHERE t.credentials = :credentials
          AND t.revokedAt IS NULL
    """)
    void revokeAllActiveByCredentials(@Param("credentials") Credentials credentials);
}
