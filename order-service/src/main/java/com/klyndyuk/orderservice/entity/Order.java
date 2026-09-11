package com.klyndyuk.orderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "order",
            orphanRemoval = true)
    private List<OrderItem> items;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "status")
    private String status;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "deleted")
    private boolean deleted;

    public void addItem(OrderItem entity) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(entity);
    }
}
