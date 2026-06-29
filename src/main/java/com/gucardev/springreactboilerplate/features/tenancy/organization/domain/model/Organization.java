package com.gucardev.springreactboilerplate.features.tenancy.organization.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The organization (tenant) aggregate — the pure domain model at the centre of the hexagon.
 *
 * <p>An organization is the top of the SaaS hierarchy: workspaces belong to one and users are scoped
 * to one. The {@code logoId} references a file in the store.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code OrganizationJpaEntity} on the way out, {@code OrganizationResponseDto} on the way in to the
 * client).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String phoneNumber;
    private String address;
    private Boolean isActive;

    /** Logo file id in the store; null if unset. Resolve its URL via {@code GET /files/{id}/url}. */
    private UUID logoId;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
