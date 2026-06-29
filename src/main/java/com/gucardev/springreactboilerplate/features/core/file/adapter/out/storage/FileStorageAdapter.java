package com.gucardev.springreactboilerplate.features.core.file.adapter.out.storage;

import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileStoragePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Driven adapter backing {@link FileStoragePort} with the configured {@link StorageProviderRegistry}.
 * Translates the port's (type, key) addressing onto the concrete providers, so the application core
 * never touches a {@link StorageProvider} or the registry directly.
 */
@Component
@RequiredArgsConstructor
public class FileStorageAdapter implements FileStoragePort {

    private final StorageProviderRegistry registry;

    @Override
    public StorageType resolveUploadTarget(StorageType requested) {
        return registry.resolveForUpload(requested).type();
    }

    @Override
    public void store(StorageType type, String key, byte[] content, String contentType) {
        registry.get(type).store(key, content, contentType);
    }

    @Override
    public byte[] retrieve(StorageType type, String key) {
        return registry.get(type).retrieve(key);
    }

    @Override
    public void delete(StorageType type, String key) {
        registry.get(type).delete(key);
    }

    @Override
    public String publicUrl(StorageType type, String key) {
        return registry.get(type).publicUrl(key);
    }

    @Override
    public Set<StorageType> activeTypes() {
        return registry.activeTypes();
    }

    @Override
    public StorageType defaultType() {
        return registry.defaultType();
    }
}
