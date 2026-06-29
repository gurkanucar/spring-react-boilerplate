package com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Role} domain model and the {@link RoleJpaEntity}. Kept hand-written
 * (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the super-builder and is
 * trivial enough to read at a glance.
 */
@Component
public class RolePersistenceMapper {

    Role toDomain(RoleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    RoleJpaEntity toEntity(Role role) {
        if (role == null) {
            return null;
        }
        return RoleJpaEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }
}
