package com.gucardev.springreactboilerplate.features.core.featureflag.entity;

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
public class FeatureFlag extends BaseEntity {

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
