package com.gucardev.springreactboilerplate.features.tenancy.workspace.adapter.out.persistence;

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
 * Persistence representation of the workspace aggregate — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model.Workspace domain
 * model} but carries all the JPA mapping so the domain stays free of infrastructure. The persistence
 * adapter maps between the two.
 *
 * <p>{@code organizationId} is the tenant discriminator: every workspace belongs to exactly one
 * organization. It is stored as a plain id column (no JPA relationship to the organization entity) so
 * this feature stays decoupled from the organization feature.
 */
@Entity
@Table(name = "workspaces",
        uniqueConstraints = @UniqueConstraint(name = "uk_workspaces_slug", columnNames = "slug"),
        indexes = {
                @Index(name = "idx_workspaces_created_at", columnList = "created_at"),
                @Index(name = "idx_workspaces_organization", columnList = "organization_id")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceJpaEntity extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String slug;

    @Column(length = 1000)
    private String description;

    @Column(length = 50)
    private String phoneNumber;

    @Column(length = 500)
    private String address;

    /** Brand accent color (hex, e.g. #b8732b) used to theme the public QR menu. */
    @Column(length = 20)
    private String brandColor;

    @Column(nullable = false)
    private Boolean isActive;

    /** Logo file id in the store; null if unset. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "logo_id")
    private UUID logoId;

    /** Owning organization (tenant). Every workspace belongs to exactly one organization. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
}
