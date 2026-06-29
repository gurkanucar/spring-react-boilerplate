package com.gucardev.springreactboilerplate.features.core.file.application.port.out;

import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: load file metadata from the store. Implemented by a driven persistence adapter.
 */
public interface LoadFilePort {

    Optional<StoredFile> findById(UUID id);

    List<StoredFile> findAllById(Collection<UUID> ids);
}
