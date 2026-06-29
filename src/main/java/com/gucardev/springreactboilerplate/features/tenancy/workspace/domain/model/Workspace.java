package com.gucardev.springreactboilerplate.features.tenancy.workspace.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The workspace aggregate — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or a web DTO. Driven adapters map to/from it
 * ({@code WorkspaceJpaEntity} on the way out, {@code WorkspaceResponseDto} on the way in to the
 * client).
 *
 * <p>{@code organizationId} is the tenant discriminator: every workspace belongs to exactly one
 * organization, and reads are scoped to the caller's organization (a super-admin sees all).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {

    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String phoneNumber;
    private String address;

    /** Brand accent color (hex, e.g. #b8732b) used to theme the public QR menu. */
    private String brandColor;

    private Boolean isActive;

    /** Logo file id in the store; null if unset. */
    private UUID logoId;

    /** Owning organization (tenant). Every workspace belongs to exactly one organization. */
    private UUID organizationId;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
