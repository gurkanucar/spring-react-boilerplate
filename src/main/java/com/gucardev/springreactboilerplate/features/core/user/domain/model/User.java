package com.gucardev.springreactboilerplate.features.core.user.domain.model;

import com.gucardev.springreactboilerplate.features.core.role.domain.model.Role;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Application user — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity ({@code UserJpaEntity}) or the web DTO. Authenticates by
 * {@code email}; authorities derive from {@link #roles} (held as domain {@link Role} objects).
 * {@code activated} reflects email verification (reserved) while {@code isActive} is the
 * enabled/disabled switch enforced via {@code UserPrincipal}.
 *
 * <p>{@code profileImageUrl}/{@code profileImageThumbnailUrl} are derived presentation values
 * resolved from {@code profileImageId} by the application services (via the file lookup port) and
 * surfaced by the web adapter — they are not persisted.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String password;
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;

    /** Id of the user's profile image in the file store (an optimized image upload); null if unset. */
    private UUID profileImageId;

    /** Tenant the user belongs to. Null = a global super-admin not scoped to any organization. */
    private UUID organizationId;

    /**
     * Workspace the user is pinned to (a workspace-level "employee"). Null = an organization-level
     * user who can act across the org's workspaces via the {@code X-Workspace-Id} header.
     */
    private UUID workspaceId;

    private Boolean activated;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // Derived presentation data resolved from profileImageId; not persisted.
    private String profileImageUrl;
    private String profileImageThumbnailUrl;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

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
