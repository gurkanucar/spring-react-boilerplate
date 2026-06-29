package com.gucardev.springreactboilerplate.features.core.role.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A grantable role — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code RoleJpaEntity} on the way out, {@code RoleResponseDto} on the way in to the client).
 *
 * <p>The {@code name} is stored without the {@code ROLE_} prefix (e.g. {@code "ADMIN"},
 * {@code "USER"}); the prefix is added when authorities are built for Spring Security, so
 * {@code @PreAuthorize("hasRole('ADMIN')")} matches. The name is immutable once created (it is the
 * key authorities are derived from); only the descriptive fields can change — see
 * {@link #updateDetails(String, String)}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private Long id;
    private String name;
    private String displayName;
    private String description;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Domain transition: edit the descriptive fields. The {@code name} is left untouched (immutable),
     * and null arguments are skipped so partial updates don't wipe existing values.
     */
    public void updateDetails(String displayName, String description) {
        if (displayName != null) {
            this.displayName = displayName;
        }
        if (description != null) {
            this.description = description;
        }
    }
}
