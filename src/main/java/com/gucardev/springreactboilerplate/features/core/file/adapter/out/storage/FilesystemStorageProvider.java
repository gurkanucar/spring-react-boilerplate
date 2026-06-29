package com.gucardev.springreactboilerplate.features.core.file.adapter.out.storage;

import com.gucardev.springreactboilerplate.features.core.file.application.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Stores files on the local filesystem under a base directory. Keys are flat (the file UUID), and
 * are validated to stay inside the base directory (defence in depth against path traversal).
 */
@Slf4j
public class FilesystemStorageProvider implements StorageProvider {

    private final Path basePath;
    private final String publicUrl;

    public FilesystemStorageProvider(StorageProperties.Filesystem config) {
        this.basePath = Path.of(config.getBasePath()).toAbsolutePath().normalize();
        this.publicUrl = config.getPublicUrl();
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create filesystem storage dir: " + basePath, e);
        }
        log.info("Filesystem storage at {}", basePath);
    }

    @Override
    public StorageType type() {
        return StorageType.FILESYSTEM;
    }

    @Override
    public void store(String key, byte[] content, String contentType) {
        try {
            Files.write(resolve(key), content);
        } catch (IOException e) {
            throw FileExceptionType.STORAGE_FAILURE.toException();
        }
    }

    @Override
    public byte[] retrieve(String key) {
        try {
            return Files.readAllBytes(resolve(key));
        } catch (IOException e) {
            throw FileExceptionType.STORAGE_FAILURE.toException();
        }
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException e) {
            log.warn("Failed to delete file {}: {}", key, e.getMessage());
        }
    }

    @Override
    public String publicUrl(String key) {
        return StringUtils.hasText(publicUrl) ? trimSlash(publicUrl) + "/" + key : null;
    }

    private Path resolve(String key) {
        Path target = basePath.resolve(key).normalize();
        if (!target.startsWith(basePath)) {
            throw FileExceptionType.STORAGE_FAILURE.toException();
        }
        return target;
    }

    private String trimSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
