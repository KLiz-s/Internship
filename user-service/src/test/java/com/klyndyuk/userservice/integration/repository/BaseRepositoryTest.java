package com.klyndyuk.userservice.integration.repository;

import com.klyndyuk.userservice.integration.BaseDatabaseTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class BaseRepositoryTest
        extends BaseDatabaseTest {

    @PersistenceContext
    protected EntityManager entityManager;
}