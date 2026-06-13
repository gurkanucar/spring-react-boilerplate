package com.gucardev.springreactboilerplate.domain.file.model.dto;

/**
 * The bytes of a file plus what the download endpoint needs to set response headers.
 */
public record FileDownload(byte[] content, String contentType, String filename) {
}
