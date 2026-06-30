package com.gucardev.springreactboilerplate.features.cart.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.cart.domain.model.CartStatus;
import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Persistence representation of the cart aggregate — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.cart.domain.model.Cart domain model} but carries
 * all the JPA mapping so the domain stays free of infrastructure. The persistence adapter maps between
 * the two.
 *
 * <p>The aggregate is stored as a <b>single row</b>: the cart's lines and coupon are denormalised into
 * {@code jsonb} columns rather than child tables. That fits a true aggregate — the lines have no life
 * or identity outside their cart, so they are always loaded and saved together with it, and there is no
 * use case that queries a line on its own. (Contrast the order feature, whose rows are queried and
 * updated independently.)
 */
@Entity
@Table(name = "carts",
        indexes = {
                @Index(name = "idx_carts_status", columnList = "status"),
                @Index(name = "idx_carts_created_at", columnList = "created_at")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CartJpaEntity extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CartStatus status;

    /** The cart's lines, serialized as a JSON array. Never null — an empty cart stores {@code []}. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<CartLineDocument> lines;

    /** The applied coupon, or null when none is applied. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private CartCouponDocument coupon;
}
