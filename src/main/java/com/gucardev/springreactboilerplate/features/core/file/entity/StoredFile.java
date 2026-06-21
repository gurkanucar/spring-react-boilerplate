package com.gucardev.springreactboilerplate.features.core.file.entity;

import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
import com.gucardev.springreactboilerplate.features.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Metadata for a stored file. The {@code id} is an application-assigned UUID that is also the basis
 * of the {@code storageKey} (UUID + extension), so the same identifier addresses the row and the
 * object in its backend. {@code storageType} records which backend holds it.
 */
@Entity
@Table(name = "files", indexes = {
        @Index(name = "idx_files_storage_key", columnList = "storage_key", unique = true)
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile extends BaseEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    /** Key of the generated thumbnail (image uploads only); null for non-image files. */
    @Column(name = "thumbnail_key")
    private String thumbnailKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StorageType storageType;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(length = 20)
    private String extension;
}
