package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.user.adapter.out.persistence.UserJpaEntity;
import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Persistence representation of a refresh token — the driven-side JPA entity. Mirrors the
 * {@link com.gucardev.springreactboilerplate.features.core.auth.domain.model.RefreshToken domain
 * model} but carries all the JPA mapping.
 *
 * <p>The {@code @ManyToOne} association targets {@link UserJpaEntity} (a JPA association target must
 * be an entity), preserving the {@code user_id} FK and its index; the persistence adapter resolves
 * that reference from the domain model's {@code userId}.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_tokens_token", columnList = "token", unique = true),
        @Index(name = "idx_refresh_tokens_user", columnList = "user_id"),
        // Supports the cleanup job's range scan on expired tokens.
        @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Uniqueness backed by idx_refresh_tokens_token (declared on @Table above).
    @Column(nullable = false, length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean revoked = false;
}
