package com.gucardev.springreactboilerplate.features.tenancy.organization.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model.Organization;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Organization} domain model and the {@link OrganizationJpaEntity}. Kept
 * hand-written (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the
 * super-builder and is trivial enough to read at a glance.
 */
@Component
public class OrganizationPersistenceMapper {

    Organization toDomain(OrganizationJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Organization.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .isActive(entity.getIsActive())
                .logoId(entity.getLogoId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    OrganizationJpaEntity toEntity(Organization organization) {
        if (organization == null) {
            return null;
        }
        return OrganizationJpaEntity.builder()
                .id(organization.getId())
                .name(organization.getName())
                .slug(organization.getSlug())
                .description(organization.getDescription())
                .phoneNumber(organization.getPhoneNumber())
                .address(organization.getAddress())
                .isActive(organization.getIsActive())
                .logoId(organization.getLogoId())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .createdBy(organization.getCreatedBy())
                .updatedBy(organization.getUpdatedBy())
                .build();
    }
}
