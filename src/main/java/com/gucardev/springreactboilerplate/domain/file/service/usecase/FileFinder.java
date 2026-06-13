package com.gucardev.springreactboilerplate.domain.file.service.usecase;

import com.gucardev.springreactboilerplate.domain.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.domain.file.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.domain.file.repository.FileRepository;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheManagers;
import com.gucardev.springreactboilerplate.infra.config.cache.CacheNames;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for the read/download/delete use cases. Metadata is cached
 * in-memory by id (small objects); {@code DeleteFileUseCase} evicts on removal. Misses (404s) are
 * not cached.
 */
@Service
@RequiredArgsConstructor
public class FileFinder {

    private final FileRepository repository;

    @Cacheable(cacheNames = CacheNames.FILE_METADATA, cacheManager = CacheManagers.CAFFEINE_10M, key = "#id")
    public StoredFile findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> FileExceptionType.NOT_FOUND.toException(id));
    }
}
