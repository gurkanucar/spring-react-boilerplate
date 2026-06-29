package com.gucardev.springreactboilerplate.features.core.featureflag.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Persistence representation of the feature-flag override — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.core.featureflag.domain.model.FeatureFlag domain
 * model} but carries all the JPA mapping so the domain stays free of infrastructure. The persistence
 * adapter maps between the two.
 */
@Entity
@Table(name = "feature_flags",
        uniqueConstraints = @UniqueConstraint(name = "uk_feature_flag_workspace_key",
                columnNames = {"workspace_id", "flag_key"}),
        indexes = @Index(name = "idx_feature_flag_workspace", columnList = "workspace_id"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagJpaEntity extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    /** Owning workspace (tenant). A flag override exists per (workspace, key). */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "flag_key", nullable = false, length = 60)
    private String flagKey;

    @Column(nullable = false)
    private Boolean enabled;
}
