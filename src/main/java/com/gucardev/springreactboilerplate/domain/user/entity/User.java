package com.gucardev.springreactboilerplate.domain.user.entity;

import com.gucardev.springreactboilerplate.domain.role.entity.Role;
import com.gucardev.springreactboilerplate.domain.shared.entity.BaseEntity;
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
 * Application user. Authenticates by {@code email}; authorities derive from {@link #roles}.
 * {@code activated} reflects email verification (reserved, not enforced at login yet) while
 * {@code isActive} is the account enabled/disabled switch enforced via {@code UserPrincipal}.
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
public class User extends BaseEntity {

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
    private Set<Role> roles = new HashSet<>();

    public Boolean getActivated() {
        return activated != null && activated;
    }

    public Boolean getIsActive() {
        return isActive == null || isActive;
    }

    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }
}
