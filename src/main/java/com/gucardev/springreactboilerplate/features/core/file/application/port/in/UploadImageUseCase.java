package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;

/**
 * Input port: store an image, optimized to WebP with a generated thumbnail, returning its persisted
 * metadata. Use {@link UploadFileUseCase} for non-image files.
 */
public interface UploadImageUseCase {

    StoredFile uploadImage(UploadFileCommand command);
}
