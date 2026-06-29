package com.gucardev.springreactboilerplate.features.core.file.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link StoredFile} domain model and the {@link FileJpaEntity}. Kept
 * hand-written (not MapStruct) because it spans the audit fields on {@code BaseEntity} via the
 * super-builder and is trivial enough to read at a glance.
 */
@Component
public class FilePersistenceMapper {

    StoredFile toDomain(FileJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return StoredFile.builder()
                .id(entity.getId())
                .originalFilename(entity.getOriginalFilename())
                .storageKey(entity.getStorageKey())
                .thumbnailKey(entity.getThumbnailKey())
                .storageType(entity.getStorageType())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .extension(entity.getExtension())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    FileJpaEntity toEntity(StoredFile file) {
        if (file == null) {
            return null;
        }
        return FileJpaEntity.builder()
                .id(file.getId())
                .originalFilename(file.getOriginalFilename())
                .storageKey(file.getStorageKey())
                .thumbnailKey(file.getThumbnailKey())
                .storageType(file.getStorageType())
                .contentType(file.getContentType())
                .size(file.getSize())
                .extension(file.getExtension())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .createdBy(file.getCreatedBy())
                .updatedBy(file.getUpdatedBy())
                .build();
    }
}
