package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import java.util.UUID;

/**
 * Input port: read a single file's metadata (or 404).
 */
public interface GetFileUseCase {

    StoredFile getById(UUID id);
}
