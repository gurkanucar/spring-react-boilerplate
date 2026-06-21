package com.gucardev.springreactboilerplate.features.core.file.service;

import com.gucardev.springreactboilerplate.features.core.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileUrlDto;
import com.gucardev.springreactboilerplate.features.core.file.storage.StorageProvider;
import com.gucardev.springreactboilerplate.features.core.file.storage.StorageProviderRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves a stored file's access URLs (and its thumbnail's): the backend's public CDN URL when it
 * has one, otherwise the app's authenticated download endpoint. Shared by the file URL endpoint and
 * by user profile-image enrichment.
 */
@Component
@RequiredArgsConstructor
public class FileUrlResolver {

    private final StorageProviderRegistry storageRegistry;

    public FileUrlDto resolve(StoredFile file) {
        StorageProvider provider = storageRegistry.get(file.getStorageType());
        String url = resolve(provider, file.getStorageKey(), file.getId(), "download");
        String thumbnailUrl = file.getThumbnailKey() != null
                ? resolve(provider, file.getThumbnailKey(), file.getId(), "thumbnail/download")
                : null;
        return new FileUrlDto(url, thumbnailUrl);
    }

    private String resolve(StorageProvider provider, String key, UUID id, String endpoint) {
        String publicUrl = provider.publicUrl(key);
        return publicUrl != null ? publicUrl : "/files/" + id + "/" + endpoint;
    }
}
