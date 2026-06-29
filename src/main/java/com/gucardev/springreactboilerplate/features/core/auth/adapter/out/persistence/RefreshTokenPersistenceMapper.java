package com.gucardev.springreactboilerplate.features.core.auth.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.auth.domain.model.RefreshToken;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link RefreshToken} domain model and the {@link RefreshTokenJpaEntity}. The
 * scalar fields (and audit) are mapped here; the {@code user} association is resolved by the
 * persistence adapter from the domain model's {@code userId}.
 */
@Component
public class RefreshTokenPersistenceMapper {

    RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return RefreshToken.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userEmail(entity.getUser() != null ? entity.getUser().getEmail() : null)
                .expiresAt(entity.getExpiresAt())
                .revoked(entity.getRevoked())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /** Maps scalar fields only; the caller must set the {@code user} association. */
    RefreshTokenJpaEntity toEntity(RefreshToken token) {
        if (token == null) {
            return null;
        }
        return RefreshTokenJpaEntity.builder()
                .id(token.getId())
                .token(token.getToken())
                .expiresAt(token.getExpiresAt())
                .revoked(token.getRevoked())
                .createdAt(token.getCreatedAt())
                .updatedAt(token.getUpdatedAt())
                .createdBy(token.getCreatedBy())
                .updatedBy(token.getUpdatedBy())
                .build();
    }
}
