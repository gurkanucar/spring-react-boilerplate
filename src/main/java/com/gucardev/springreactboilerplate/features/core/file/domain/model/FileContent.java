package com.gucardev.springreactboilerplate.features.core.file.domain.model;

/**
 * The bytes of a file plus what the download endpoint needs to set response headers.
 */
public record FileContent(byte[] content, String contentType, String filename) {
}
