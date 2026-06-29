package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlag;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link FeatureFlag} domain model and the {@link FeatureFlagJpaEntity}. Kept
 * hand-written (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the
 * super-builder and is trivial enough to read at a glance.
 */
@Component
public class FeatureFlagPersistenceMapper {

    FeatureFlag toDomain(FeatureFlagJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return FeatureFlag.builder()
                .id(entity.getId())
                .workspaceId(entity.getWorkspaceId())
                .flagKey(entity.getFlagKey())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    FeatureFlagJpaEntity toEntity(FeatureFlag featureFlag) {
        if (featureFlag == null) {
            return null;
        }
        return FeatureFlagJpaEntity.builder()
                .id(featureFlag.getId())
                .workspaceId(featureFlag.getWorkspaceId())
                .flagKey(featureFlag.getFlagKey())
                .enabled(featureFlag.getEnabled())
                .createdAt(featureFlag.getCreatedAt())
                .updatedAt(featureFlag.getUpdatedAt())
                .createdBy(featureFlag.getCreatedBy())
                .updatedBy(featureFlag.getUpdatedBy())
                .build();
    }
}
