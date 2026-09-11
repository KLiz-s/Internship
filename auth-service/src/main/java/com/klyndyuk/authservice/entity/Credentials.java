package com.klyndyuk.authservice.entity;

import com.klyndyuk.authservice.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "credentials")
public class Credentials {
    @Id
    @GeneratedValue
    private UUID userId;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", nullable = false)
    private Role role;

}
