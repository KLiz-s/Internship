package com.klyndyuk.userservice.repository;

import com.klyndyuk.userservice.entity.User;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    @Query("""
       update User u
       set u.active = :active
       where u.id = :id
       """)
    @Modifying
    int updateActive(UUID id, boolean active);

    @EntityGraph(attributePaths = {"paymentCards"})
    Optional<User> findWithPaymentCardsById(UUID id);
}
