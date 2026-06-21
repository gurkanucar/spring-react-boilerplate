package com.gucardev.springreactboilerplate.features.core.auth.entity;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import com.gucardev.springreactboilerplate.features.core.user.entity.User;
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
 * A persisted, opaque refresh token (rotated on every refresh, revocable on logout). Expired or
 * revoked rows are purged periodically by {@code RefreshTokenCleanupJob}.
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
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Uniqueness backed by idx_refresh_tokens_token (declared on @Table above).
    @Column(nullable = false, length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean revoked = false;

    public Boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public Boolean isUsable() {
        return !Boolean.TRUE.equals(revoked) && !isExpired();
    }
}
