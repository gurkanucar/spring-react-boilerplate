package com.gucardev.springreactboilerplate.features.core.file.domain.model;

/**
 * The outcome of validating an upload: the sanitized display name, the resolved extension (consistent
 * with the real content), the magic-byte-detected content type, and the raw bytes to store.
 */
public record ValidatedUpload(String sanitizedFilename, String extension, String contentType, byte[] content) {
}
