package com.klyndyuk.orderservice.repository;

import com.klyndyuk.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    List<Order> findAllByUserIdAndDeletedFalse(UUID userId);

    Optional<Order> findByIdAndDeletedFalse(UUID id);

    default Page<Order> findAllByDeletedFalse(Specification<Order> specification, Pageable pageable) {
        Specification<Order> notDeleted =
                (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));

        return findAll(Specification.where(notDeleted).and(specification), pageable);
    }

}
