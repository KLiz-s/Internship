package com.klyndyuk.userservice.repository;

import com.klyndyuk.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    @Query("""
       update User u
       set u.active = :active, u.updatedAt = CURRENT_TIMESTAMP
       where u.id = :id
       """)
    @Modifying
    int updateActive(UUID id, boolean active);

    boolean existsByEmail(String email);
}
