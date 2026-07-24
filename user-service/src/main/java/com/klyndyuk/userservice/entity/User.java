package com.klyndyuk.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PaymentCard> paymentCards = new ArrayList<>();

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email")
    private String email;

    @Column(name = "active")
    private boolean active = true;

    public void addPaymentCard(PaymentCard paymentCard) {
        if (!paymentCards.contains(paymentCard)) {
            paymentCards.add(paymentCard);
            paymentCard.setUser(this);
        }
    }

    public void removePaymentCard(PaymentCard paymentCard) {
        if (paymentCards.remove(paymentCard)) {
            paymentCard.setUser(null);
        }
    }
}
