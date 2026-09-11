package com.klyndyuk.orderservice.specification;

import com.klyndyuk.orderservice.dto.response.OrderResponse;
import com.klyndyuk.orderservice.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class OrderSpecification {
    private OrderSpecification() {
    }

    public static Specification<Order> fromDate(Instant fromDate) {
        return (root, query, criteriaBuilder) ->
            fromDate == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
    }

    public static Specification<Order> toDate(Instant toDate) {
        return (root, query, criteriaBuilder) ->
             toDate == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate);
    }

    public static Specification<Order> status(String status) {
        return (root, query, criteriaBuilder) ->
            status == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Order> byFilters(Instant fromDate, Instant toDate, String status) {
        return Specification
                .where(fromDate(fromDate))
                .and(toDate(toDate))
                .and(status(status));
    }
}
