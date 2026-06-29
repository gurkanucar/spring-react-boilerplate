package com.gucardev.springreactboilerplate.features.core.file.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Metadata for a stored file — the pure domain model at the centre of the hexagon.
 *
 * <p>The {@code id} is an application-assigned UUID that is also the basis of the {@code storageKey}
 * (UUID + extension), so the same identifier addresses the row and the object in its backend.
 * {@code storageType} records which backend holds it.
 *
 * <p>It carries no JPA, Spring or serialization annotations: the application core depends on this
 * class, never on the persistence entity or the web DTO. Driven adapters map to/from it.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile {

    private UUID id;
    private String originalFilename;
    private String storageKey;

    /** Key of the generated thumbnail (image uploads only); null for non-image files. */
    private String thumbnailKey;

    private StorageType storageType;
    private String contentType;
    private Long size;
    private String extension;

    // Audit metadata, carried so adapters can surface it without reaching into the persistence entity.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
