package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;

/**
 * Driving-side command carrying an upload from a driving adapter into the application core: the
 * client-supplied filename, the raw bytes, and the target backend ({@code null} = the configured
 * default). Decoupled from any transport type (e.g. {@code MultipartFile}).
 */
public record UploadFileCommand(String originalFilename, byte[] content, StorageType storageType) {
}
