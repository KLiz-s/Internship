package com.klyndyuk.userservice.repository;

import com.klyndyuk.userservice.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID> {
    Page<PaymentCard> findAllByHolder(String holder, Pageable pageable);

    @Modifying
    @Query("""
       update PaymentCard c
       set c.active = :active, c.updatedAt = CURRENT_TIMESTAMP
       where c.id = :id
       """)
    int updateActive(UUID id, boolean active);

    @Query(
            value = """
            SELECT *
            FROM payment_cards
            WHERE user_id = :userId
            """,
            nativeQuery = true
    )
    List<PaymentCard> findAllByUserId(UUID userId);
}
