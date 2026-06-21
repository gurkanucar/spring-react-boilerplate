package com.gucardev.springreactboilerplate.features.core.file.storage;

import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;

/**
 * A pluggable storage backend. Files are addressed by an opaque {@code key} (the file's UUID plus
 * extension). Implementations: filesystem, S3-compatible (RustFS/MinIO/AWS), Cloudflare R2.
 */
public interface StorageProvider {

    StorageType type();

    void store(String key, byte[] content, String contentType);

    byte[] retrieve(String key);

    void delete(String key);

    /**
     * Public URL for direct (CDN) access, or {@code null} when this backend exposes no public URL —
     * in which case the caller falls back to the app's authenticated download endpoint.
     */
    String publicUrl(String key);
}
