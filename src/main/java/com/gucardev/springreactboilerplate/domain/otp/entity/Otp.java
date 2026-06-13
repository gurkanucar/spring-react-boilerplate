package com.gucardev.springreactboilerplate.domain.otp.entity;

import com.gucardev.springreactboilerplate.domain.otp.enums.OtpSendingChannel;
import com.gucardev.springreactboilerplate.domain.otp.enums.OtpType;
import com.gucardev.springreactboilerplate.domain.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A one-time password issued for a {@code (destination, type)} pair. The code is stored as given
 * (never returned in any response). At most one OTP is active per {@code (destination, type)} —
 * sending a new one invalidates the previous. Expired/used rows are purged by {@code OtpCleanupJob}.
 */
@Entity
@Table(name = "otps", indexes = {
        @Index(name = "idx_otps_destination_type", columnList = "destination,type"),
        @Index(name = "idx_otps_expiry_time", columnList = "expiry_time")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Otp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OtpType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OtpSendingChannel sendingChannel;

    @Column(nullable = false, length = 8)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Builder.Default
    @Column(nullable = false)
    private Boolean used = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer attempts = 0;

    public boolean isExpired() {
        return expiryTime.isBefore(LocalDateTime.now());
    }
}
