package com.klyndyuk.paymentservice.repository;

import com.klyndyuk.paymentservice.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends MongoRepository<Payment, UUID> {
    @Query("{'orderId': ?0}")
    Page<Payment> findByOrderId(UUID orderId, Pageable pageable);

    @Query("{'orderId': ?0, 'userId': ?1}")
    Page<Payment> findByOrderIdAndUserId(UUID orderId, UUID userId, Pageable pageable);

    @Query("{'userId': ?0}")
    Page<Payment> findByUserId(UUID userId, Pageable pageable);

    @Query("{'status': ?0}")
    Page<Payment> findByStatus(String status, Pageable pageable);

    @Query("{'userId': ?0, 'timestamp': {$gte: ?1, $lte: ?2}}")
    List<Payment> findByUserIdAndTimestampBetween(UUID userId, Instant startDate, Instant endDate);

    @Query("{'timestamp': {$gte: ?0, $lte: ?1}}")
    List<Payment> findByTimestampBetween(Instant startDate, Instant endDate);
}
