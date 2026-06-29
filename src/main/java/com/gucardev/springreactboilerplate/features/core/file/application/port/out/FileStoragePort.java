package com.gucardev.springreactboilerplate.features.core.file.application.port.out;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import java.util.Set;

/**
 * Output port for the storage backend(s) — writing/reading/deleting the raw bytes of a file, keyed by
 * an opaque {@code key} (the file's UUID plus extension). Implemented by a driven adapter that fronts
 * the local disk and/or S3-compatible backends. The application core depends only on this port.
 */
public interface FileStoragePort {

    /**
     * Resolves the backend a new upload should target: the requested one if given (must be active,
     * else a 400), otherwise the configured default. Returns the effective {@link StorageType}.
     */
    StorageType resolveUploadTarget(StorageType requested);

    void store(StorageType type, String key, byte[] content, String contentType);

    byte[] retrieve(StorageType type, String key);

    void delete(StorageType type, String key);

    /**
     * Public URL for direct (CDN) access, or {@code null} when the backend exposes no public URL — in
     * which case the caller falls back to the app's authenticated download endpoint.
     */
    String publicUrl(StorageType type, String key);

    Set<StorageType> activeTypes();

    StorageType defaultType();
}
