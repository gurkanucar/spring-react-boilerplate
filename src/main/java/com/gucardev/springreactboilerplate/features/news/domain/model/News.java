package com.gucardev.springreactboilerplate.features.news.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The news aggregate — the pure domain model at the centre of the hexagon.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it
 * ({@code NewsJpaEntity} on the way out, {@code NewsResponseDto} on the way in to the client).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {

    private UUID id;

    /** Owning workspace (tenant). Every news entry belongs to exactly one workspace. */
    private UUID workspaceId;

    private String title;

    /** URL-safe slug, auto-generated from the title; unique within a workspace. */
    private String slug;

    private String content;

    private Boolean featured;

    /** File id (in the file store) of the featured image; must be one of {@link #imageIds}. */
    private UUID featuredImageId;

    /** Ordered file ids of the attached images. Files are uploaded via the file API and referenced by id. */
    @Builder.Default
    private List<UUID> imageIds = new ArrayList<>();

    /** Ordered file ids of attached documents (any file type). Uploaded via the file API and referenced by id. */
    @Builder.Default
    private List<UUID> attachmentIds = new ArrayList<>();

    @Builder.Default
    private Set<String> tags = new HashSet<>();

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /** True when there is no featured image, or it is present among {@link #imageIds}. */
    public boolean isFeaturedImageConsistent() {
        return featuredImageId == null || (imageIds != null && imageIds.contains(featuredImageId));
    }
}
