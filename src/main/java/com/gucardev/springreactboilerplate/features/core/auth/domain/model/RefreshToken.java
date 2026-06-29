package com.gucardev.springreactboilerplate.features.core.auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A persisted, opaque refresh token (rotated on every refresh, revocable on logout) — the pure
 * domain model. Carries the owning user's id and email (so a fresh token bundle can be minted on
 * refresh) rather than a reference to the user entity. Expired or revoked tokens are purged
 * periodically by the cleanup job.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private Long id;
    private String token;
    private UUID userId;
    private String userEmail;
    private LocalDateTime expiresAt;

    @Builder.Default
    private Boolean revoked = false;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public Boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public Boolean isUsable() {
        return !Boolean.TRUE.equals(revoked) && !isExpired();
    }

    /** Domain transition: revoke this token (rotation on refresh, or logout). */
    public void revoke() {
        this.revoked = true;
    }
}
