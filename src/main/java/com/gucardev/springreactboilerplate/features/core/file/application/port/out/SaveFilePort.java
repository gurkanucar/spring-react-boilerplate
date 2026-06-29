package com.gucardev.springreactboilerplate.features.core.file.application.port.out;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;

/**
 * Output port: persist file metadata (insert or update) and return the stored state.
 */
public interface SaveFilePort {

    StoredFile save(StoredFile file);
}
