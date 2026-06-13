package com.gucardev.springreactboilerplate.domain.file.service.usecase;

import com.gucardev.springreactboilerplate.domain.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.domain.file.repository.FileRepository;
import com.gucardev.springreactboilerplate.domain.file.storage.StorageProvider;
import com.gucardev.springreactboilerplate.domain.file.storage.StorageProviderRegistry;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteFileUseCase {

    private final FileFinder finder;
    private final FileRepository repository;
    private final StorageProviderRegistry storageRegistry;

    @CacheEvict(cacheNames = CacheNames.FILE_METADATA, cacheManager = CacheManagers.CAFFEINE_10M, key = "#id")
    @Transactional
    public void execute(UUID id) {
        StoredFile file = finder.findById(id);
        repository.delete(file);

        StorageProvider provider = storageRegistry.get(file.getStorageType());
        provider.delete(file.getStorageKey());
        if (file.getThumbnailKey() != null) {
            provider.delete(file.getThumbnailKey());
        }
    }
}
