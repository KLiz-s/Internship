package com.klyndyuk.orderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
}