package com.klyndyuk.userservice.repository;

import com.klyndyuk.userservice.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    @Query("""
       update User u
       set u.active = :active, u.updatedAt = CURRENT_TIMESTAMP
       where u.id = :id
       """)
    @Modifying
    int updateActive(@Param("id") UUID id, @Param("active") boolean active);

    @EntityGraph(attributePaths = {"paymentCards"})
    Optional<User> findWithPaymentCardsById(UUID id);

    boolean existsByEmail(String email);
}
