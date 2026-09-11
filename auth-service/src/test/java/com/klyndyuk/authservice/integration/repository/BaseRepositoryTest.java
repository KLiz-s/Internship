package com.klyndyuk.authservice.integration.repository;

import com.klyndyuk.authservice.integration.BaseDatabaseTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class BaseRepositoryTest
        extends BaseDatabaseTest {

    @PersistenceContext
    protected EntityManager entityManager;
}