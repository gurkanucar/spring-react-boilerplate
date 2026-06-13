package com.gucardev.springreactboilerplate.domain.file.service.usecase;

import com.gucardev.springreactboilerplate.domain.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.domain.file.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.domain.file.repository.FileRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Shared "fetch or 404" lookup for the read/download/delete use cases.
 */
@Service
@RequiredArgsConstructor
public class FileFinder {

    private final FileRepository repository;

    public StoredFile findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> FileExceptionType.NOT_FOUND.toException(id));
    }
}
