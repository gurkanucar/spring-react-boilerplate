package com.gucardev.springreactboilerplate.domain.file.service.usecase;

import com.gucardev.springreactboilerplate.domain.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.domain.file.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.domain.file.model.dto.FileDownload;
import com.gucardev.springreactboilerplate.domain.file.service.FilenameSanitizer;
import com.gucardev.springreactboilerplate.domain.file.storage.StorageProviderRegistry;
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
public class DownloadFileUseCase {

    private final FileFinder finder;
    private final StorageProviderRegistry storageRegistry;

    @Transactional(readOnly = true)
    public FileDownload execute(UUID id) {
        StoredFile file = finder.findById(id);
        byte[] content = storageRegistry.get(file.getStorageType()).retrieve(file.getStorageKey());
        return new FileDownload(content, file.getContentType(), file.getOriginalFilename());
    }

    @Transactional(readOnly = true)
    public FileDownload executeThumbnail(UUID id) {
        StoredFile file = finder.findById(id);
        if (file.getThumbnailKey() == null) {
            throw FileExceptionType.NOT_FOUND.toException(id);
        }
        byte[] content = storageRegistry.get(file.getStorageType()).retrieve(file.getThumbnailKey());
        String filename = FilenameSanitizer.stripExtension(file.getOriginalFilename()) + "_tb" + file.getExtension();
        return new FileDownload(content, file.getContentType(), filename);
    }
}
