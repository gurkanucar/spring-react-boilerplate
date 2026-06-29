package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Workspace} domain model and the {@link WorkspaceJpaEntity}. Kept
 * hand-written (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the
 * super-builder and is trivial enough to read at a glance.
 */
@Component
public class WorkspacePersistenceMapper {

    Workspace toDomain(WorkspaceJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Workspace.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .brandColor(entity.getBrandColor())
                .isActive(entity.getIsActive())
                .logoId(entity.getLogoId())
                .organizationId(entity.getOrganizationId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    WorkspaceJpaEntity toEntity(Workspace workspace) {
        if (workspace == null) {
            return null;
        }
        return WorkspaceJpaEntity.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .slug(workspace.getSlug())
                .description(workspace.getDescription())
                .phoneNumber(workspace.getPhoneNumber())
                .address(workspace.getAddress())
                .brandColor(workspace.getBrandColor())
                .isActive(workspace.getIsActive())
                .logoId(workspace.getLogoId())
                .organizationId(workspace.getOrganizationId())
                .createdAt(workspace.getCreatedAt())
                .updatedAt(workspace.getUpdatedAt())
                .createdBy(workspace.getCreatedBy())
                .updatedBy(workspace.getUpdatedBy())
                .build();
    }
}
