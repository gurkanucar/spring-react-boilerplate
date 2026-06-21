package com.gucardev.springreactboilerplate.features.core.file.storage;

import com.gucardev.springreactboilerplate.features.core.file.enums.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.exception.FileExceptionType;
import jakarta.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Holds the active {@link StorageProvider}s and knows which one new uploads target. Existing files
 * resolve to {@link #get(StorageType)} by their stored type; new uploads use {@link #getDefault()}.
 * Fails fast at startup if the configured default backend isn't actually active.
 */
@Slf4j
public class StorageProviderRegistry {

    private final Map<StorageType, StorageProvider> providers;
    private final StorageType defaultType;

    public StorageProviderRegistry(Map<StorageType, StorageProvider> providers, StorageType defaultType) {
        this.providers = providers;
        this.defaultType = defaultType;
        if (!providers.containsKey(defaultType)) {
            throw new IllegalStateException(
                    "storage.default-type=" + defaultType + " is not an active backend. Active: "
                            + providers.keySet() + ". Configure it or change the default.");
        }
        log.info("Storage backends active: {} (default: {})", providers.keySet(), defaultType);
    }

    public StorageProvider getDefault() {
        return get(defaultType);
    }

    public StorageType defaultType() {
        return defaultType;
    }

    public java.util.Set<StorageType> activeTypes() {
        return java.util.Collections.unmodifiableSet(providers.keySet());
    }

    /**
     * Picks the backend for a new upload: the requested one if given (must be active, else a 400),
     * otherwise the configured default.
     */
    public StorageProvider resolveForUpload(StorageType requested) {
        if (requested == null) {
            return getDefault();
        }
        StorageProvider provider = providers.get(requested);
        if (provider == null) {
            throw FileExceptionType.STORAGE_BACKEND_NOT_ACTIVE.toException(requested, activeTypes());
        }
        return provider;
    }

    public StorageProvider get(StorageType type) {
        StorageProvider provider = providers.get(type);
        if (provider == null) {
            throw FileExceptionType.STORAGE_NOT_CONFIGURED.toException(type);
        }
        return provider;
    }

    @PreDestroy
    public void shutdown() {
        providers.values().forEach(provider -> {
            if (provider instanceof Closeable closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    log.warn("Error closing storage provider {}: {}", provider.type(), e.getMessage());
                }
            }
        });
    }
}
