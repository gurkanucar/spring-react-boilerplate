package com.gucardev.springreactboilerplate.features.core.file.application.port.in;

import java.util.UUID;

/**
 * Input port: delete a file — its metadata, the stored object and any thumbnail.
 */
public interface DeleteFileUseCase {

    void delete(UUID id);
}
