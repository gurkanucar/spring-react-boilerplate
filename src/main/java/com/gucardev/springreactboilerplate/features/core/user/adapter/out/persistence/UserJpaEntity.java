package com.gucardev.springreactboilerplate.features.core.user.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.role.adapter.out.persistence.RoleJpaEntity;
import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Persistence representation of the application user — the driven-side JPA entity. It mirrors the
 * {@link com.gucardev.springreactboilerplate.features.core.user.domain.model.User domain model} but
 * carries all the JPA mapping so the domain stays free of infrastructure. The persistence adapter
 * maps between the two.
 *
 * <p>The {@code @ManyToMany} association targets {@link RoleJpaEntity} (a JPA association target must
 * be an entity); the persistence mapper converts those to/from domain roles.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        indexes = @Index(name = "idx_users_created_at", columnList = "created_at"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    private String password;

    // Uniqueness declared at the table level (uk_users_email); that constraint also backs
    // the index used by the email-based login/lookup queries.
    @Column(nullable = false)
    private String email;

    private String name;

    private String surname;

    private String phoneNumber;

    /** Id of the user's profile image in the file store (an optimized image upload); null if unset. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "profile_image_id")
    private UUID profileImageId;

    /** Tenant the user belongs to. Null = a global super-admin not scoped to any organization. */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "organization_id")
    private UUID organizationId;

    /**
     * Workspace the user is pinned to (a workspace-level "employee"). Null = an organization-level
     * user (e.g. a manager) who can act across the org's workspaces via the {@code X-Workspace-Id}
     * header.
     */
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "workspace_id")
    private UUID workspaceId;

    private Boolean activated;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE NOT NULL")
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            indexes = {@Index(name = "idx_user_roles_role", columnList = "role_id")})
    @Builder.Default
    private Set<RoleJpaEntity> roles = new HashSet<>();

    public Boolean getActivated() {
        return activated != null && activated;
    }

    public Boolean getIsActive() {
        return isActive == null || isActive;
    }
}
