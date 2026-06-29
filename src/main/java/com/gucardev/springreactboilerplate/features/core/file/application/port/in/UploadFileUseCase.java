package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;

/**
 * Input port: store an arbitrary file as-is (after validation), returning its persisted metadata.
 * Driving adapters and other features depend on this interface, not on the implementing service.
 */
public interface UploadFileUseCase {

    StoredFile upload(UploadFileCommand command);
}
