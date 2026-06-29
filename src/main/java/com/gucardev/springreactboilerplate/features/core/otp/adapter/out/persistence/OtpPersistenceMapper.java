package com.gucardev.springreactboilerplate.features.core.otp.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Otp} domain model and the {@link OtpJpaEntity}. Kept hand-written (not
 * MapStruct) because it spans the audit fields on {@code BaseEntity} via the super-builder and is
 * trivial enough to read at a glance.
 */
@Component
public class OtpPersistenceMapper {

    Otp toDomain(OtpJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Otp.builder()
                .id(entity.getId())
                .destination(entity.getDestination())
                .type(entity.getType())
                .sendingChannel(entity.getSendingChannel())
                .code(entity.getCode())
                .expiryTime(entity.getExpiryTime())
                .used(entity.getUsed())
                .attempts(entity.getAttempts())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    OtpJpaEntity toEntity(Otp otp) {
        if (otp == null) {
            return null;
        }
        return OtpJpaEntity.builder()
                .id(otp.getId())
                .destination(otp.getDestination())
                .type(otp.getType())
                .sendingChannel(otp.getSendingChannel())
                .code(otp.getCode())
                .expiryTime(otp.getExpiryTime())
                .used(otp.getUsed())
                .attempts(otp.getAttempts())
                .createdAt(otp.getCreatedAt())
                .updatedAt(otp.getUpdatedAt())
                .createdBy(otp.getCreatedBy())
                .updatedBy(otp.getUpdatedBy())
                .build();
    }
}
