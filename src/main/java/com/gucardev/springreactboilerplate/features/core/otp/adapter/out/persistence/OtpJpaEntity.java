package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpSendingChannel;
import com.gucardev.springreactboilerplate.features.core.otp.domain.model.OtpType;
import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
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
 * Persistence representation of the OTP aggregate — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp domain model} but
 * carries all the JPA mapping so the domain stays free of infrastructure. The persistence adapter
 * maps between the two.
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
public class OtpJpaEntity extends BaseEntity {

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
}
