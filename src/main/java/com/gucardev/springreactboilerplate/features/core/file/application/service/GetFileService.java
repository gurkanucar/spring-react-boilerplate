package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.port.in.GetFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads a single file's metadata (or 404).
 */
@Service
@RequiredArgsConstructor
public class GetFileService implements GetFileUseCase {

    private final FileFinder finder;

    @Override
    @Transactional(readOnly = true)
    public StoredFile getById(UUID id) {
        return finder.findById(id);
    }
}
