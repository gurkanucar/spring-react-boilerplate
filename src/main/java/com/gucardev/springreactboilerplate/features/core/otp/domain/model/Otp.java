package com.gucardev.springreactboilerplate.features.core.otp.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The OTP aggregate — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code OtpJpaEntity} on the way out, {@code OtpResponseDto} on the way in to the client).
 *
 * <p>A one-time password is issued for a {@code (destination, type)} pair. The code is stored as
 * given (never returned in any response). At most one OTP is active per {@code (destination, type)} —
 * sending a new one invalidates the previous. Expired/used rows are purged by the cleanup job.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    private Long id;
    private String destination;
    private OtpType type;
    private OtpSendingChannel sendingChannel;
    private String code;
    private LocalDateTime expiryTime;

    @Builder.Default
    private Boolean used = false;

    @Builder.Default
    private Integer attempts = 0;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /** True once the validity window has elapsed. */
    public boolean isExpired() {
        return expiryTime.isBefore(LocalDateTime.now());
    }

    /** Domain transition: burn the OTP so it can never be used again (single use). */
    public void markUsed() {
        this.used = true;
    }

    /** Domain transition: record a failed verification attempt. */
    public void incrementAttempts() {
        this.attempts = this.attempts + 1;
    }

    /** Whether the failed-attempt counter has reached the configured lock-out threshold. */
    public boolean hasReachedMaxAttempts(int maxAttempts) {
        return this.attempts >= maxAttempts;
    }

    /** Constant-shape equality check of a submitted code against the stored one. */
    public boolean matches(String candidate) {
        return this.code.equals(candidate);
    }
}
