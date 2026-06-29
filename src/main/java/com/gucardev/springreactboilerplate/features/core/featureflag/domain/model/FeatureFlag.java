package com.gucardev.springreactboilerplate.features.core.featureflag.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The feature-flag aggregate — the pure domain model at the centre of the hexagon. It represents a
 * single per-workspace override of a flag (a row of {@code (workspaceId, flagKey, enabled)}).
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code FeatureFlagJpaEntity} on the way out).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlag {

    private UUID id;

    /** Owning workspace (tenant). A flag override exists per (workspace, key). */
    private UUID workspaceId;

    private String flagKey;
    private Boolean enabled;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /** Domain transition: turn the flag on for the workspace. */
    public void enable() {
        this.enabled = true;
    }

    /** Domain transition: turn the flag off for the workspace. */
    public void disable() {
        this.enabled = false;
    }
}
