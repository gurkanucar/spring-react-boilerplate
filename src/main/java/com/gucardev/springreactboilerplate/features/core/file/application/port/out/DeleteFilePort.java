package com.gucardev.springreactboilerplate.features.core.file.application.port.out;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;

/**
 * Output port: remove a file's metadata row from the store.
 */
public interface DeleteFilePort {

    void delete(StoredFile file);
}
