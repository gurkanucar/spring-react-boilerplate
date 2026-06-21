package com.gucardev.springreactboilerplate.features.core.file.service.usecase;

import com.gucardev.springreactboilerplate.features.core.file.model.dto.FileUrlDto;
import com.gucardev.springreactboilerplate.features.core.file.service.FileUrlResolver;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves a file's access URLs (main + thumbnail) via {@link FileUrlResolver}.
 */
@Service
@RequiredArgsConstructor
public class GetFileUrlUseCase {

    private final FileFinder finder;
    private final FileUrlResolver fileUrlResolver;

    @Transactional(readOnly = true)
    public FileUrlDto execute(UUID id) {
        return fileUrlResolver.resolve(finder.findById(id));
    }
}
