package com.klyndyuk.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
@NoArgsConstructor
public class PaymentCard extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "number")
    private String number;

    @Column(name = "holder")
    private String holder;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "active")
    private boolean active;
}
