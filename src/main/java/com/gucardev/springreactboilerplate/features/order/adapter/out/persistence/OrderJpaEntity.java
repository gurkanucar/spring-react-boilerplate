package com.gucardev.springreactboilerplate.features.order.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import com.gucardev.springreactboilerplate.features.order.domain.model.OrderStatus;

/**
 * Persistence representation of the order aggregate — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.order.domain.model.Order domain model} but
 * carries all the JPA mapping so the domain stays free of infrastructure. The persistence adapter
 * maps between the two.
 *
 * <p>{@code @Table(name = "orders")}: {@code order} is a SQL reserved word, hence the plural table name.
 */
@Entity
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_orders_status", columnList = "status"),
                @Index(name = "idx_orders_created_at", columnList = "created_at")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderJpaEntity extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(nullable = false, length = 150)
    private String product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
}
