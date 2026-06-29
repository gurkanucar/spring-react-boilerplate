package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.DownloadFileUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileStoragePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.FileContent;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Streams a file's bytes back through the app (works for any backend). Metadata comes from
 * {@link FileFinder} (cached, no DB hit on repeats); the bytes are read from the backend.
 */
@Service
@RequiredArgsConstructor
public class DownloadFileService implements DownloadFileUseCase {

    private final FileFinder finder;
    private final FileStoragePort fileStoragePort;

    @Override
    @Transactional(readOnly = true)
    public FileContent download(UUID id) {
        StoredFile file = finder.findById(id);
        byte[] content = fileStoragePort.retrieve(file.getStorageType(), file.getStorageKey());
        return new FileContent(content, file.getContentType(), file.getOriginalFilename());
    }

    @Override
    @Transactional(readOnly = true)
    public FileContent downloadThumbnail(UUID id) {
        StoredFile file = finder.findById(id);
        if (file.getThumbnailKey() == null) {
            throw FileExceptionType.NOT_FOUND.toException(id);
        }
        byte[] content = fileStoragePort.retrieve(file.getStorageType(), file.getThumbnailKey());
        String filename = FilenameSanitizer.stripExtension(file.getOriginalFilename()) + "_tb" + file.getExtension();
        return new FileContent(content, file.getContentType(), filename);
    }
}
