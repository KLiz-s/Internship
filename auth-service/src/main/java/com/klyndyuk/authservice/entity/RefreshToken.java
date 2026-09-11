package com.klyndyuk.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Credentials credentials;

    @Column(name = "token")
    private String tokenHash;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "revoked_at")
    private Date revokedAt;

    @ManyToOne
    @JoinColumn(name = "replaced_by")
    private RefreshToken replacedBy;
}
