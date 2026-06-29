package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileStoragePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileUrl;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves a stored file's access URLs (and its thumbnail's): the backend's public CDN URL when it
 * has one, otherwise the app's authenticated download endpoint.
 */
@Component
@RequiredArgsConstructor
public class FileUrlResolver {

    private final FileStoragePort fileStoragePort;

    public FileUrl resolve(StoredFile file) {
        String url = resolve(file.getStorageType(), file.getStorageKey(), file.getId(), "download");
        String thumbnailUrl = file.getThumbnailKey() != null
                ? resolve(file.getStorageType(), file.getThumbnailKey(), file.getId(), "thumbnail/download")
                : null;
        return new FileUrl(url, thumbnailUrl);
    }

    private String resolve(StorageType type, String key, UUID id, String endpoint) {
        String publicUrl = fileStoragePort.publicUrl(type, key);
        return publicUrl != null ? publicUrl : "/files/" + id + "/" + endpoint;
    }
}
