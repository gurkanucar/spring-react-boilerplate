package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.port.in.DeleteFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.DeleteFilePort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileStoragePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Deletes a file: its metadata row, the stored object and any thumbnail. Evicts the cached metadata.
 */
@Service
@RequiredArgsConstructor
public class DeleteFileService implements DeleteFileUseCase {

    private final FileFinder finder;
    private final DeleteFilePort deleteFilePort;
    private final FileStoragePort fileStoragePort;

    @Override
    @CacheEvict(cacheNames = CacheNames.FILE_METADATA, cacheManager = CacheManagers.CAFFEINE_10M, key = "#id")
    @Transactional
    public void delete(UUID id) {
        StoredFile file = finder.findById(id);
        deleteFilePort.delete(file);

        fileStoragePort.delete(file.getStorageType(), file.getStorageKey());
        if (file.getThumbnailKey() != null) {
            fileStoragePort.delete(file.getStorageType(), file.getThumbnailKey());
        }
    }
}
