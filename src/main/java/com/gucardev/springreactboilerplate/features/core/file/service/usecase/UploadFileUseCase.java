package com.gucardev.springreactboilerplate.features.core.file.service.usecase;

import com.gucardev.springreactboilerplate.features.core.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.mapper.FileMapper;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileResponseDto;
import com.gucardev.springreactboilerplate.features.core.file.repository.FileRepository;
import com.gucardev.springreactboilerplate.features.core.file.service.FileValidator;
import com.gucardev.springreactboilerplate.features.core.file.service.ValidatedUpload;
import com.gucardev.springreactboilerplate.features.core.file.storage.StorageProvider;
import com.gucardev.springreactboilerplate.features.core.file.storage.StorageProviderRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates an upload (size, extension allow/deny, magic-byte content check), assigns a UUID, writes
 * the bytes to the default backend keyed by {@code uuid + extension}, and persists the metadata.
 *
 * <p>Metadata is saved before the bytes are written, inside the transaction: if the backend write
 * fails the row is rolled back, so there is never a metadata row pointing at a missing object.
 */
@Service
@RequiredArgsConstructor
public class UploadFileUseCase {

    private final FileValidator validator;
    private final StorageProviderRegistry storageRegistry;
    private final FileRepository repository;
    private final FileMapper fileMapper;

    /**
     * @param storageType backend to store to; {@code null} uses the configured default.
     */
    @Transactional
    public FileResponseDto execute(MultipartFile file, StorageType storageType) {
        ValidatedUpload upload = validator.validate(file);

        UUID id = UUID.randomUUID();
        String key = id + upload.extension();
        StorageProvider provider = storageRegistry.resolveForUpload(storageType);

        StoredFile stored = StoredFile.builder()
                .id(id)
                .originalFilename(upload.sanitizedFilename())
                .storageKey(key)
                .storageType(provider.type())
                .contentType(upload.contentType())
                .size((long) upload.content().length)
                .extension(upload.extension())
                .build();
        repository.save(stored);

        provider.store(key, upload.content(), upload.contentType());

        return fileMapper.toDto(stored);
    }
}
