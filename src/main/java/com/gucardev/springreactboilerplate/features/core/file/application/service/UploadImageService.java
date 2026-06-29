package com.gucardev.springreactboilerplate.features.core.file.application.service;

import com.gucardev.springreactboilerplate.features.core.file.application.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadFileCommand;
import com.gucardev.springreactboilerplate.features.core.file.application.port.in.UploadImageUseCase;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileStoragePort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.FileValidationPort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.ImageProcessingPort;
import com.gucardev.springreactboilerplate.features.core.file.application.port.out.SaveFilePort;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.OptimizedImage;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StorageType;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.StoredFile;
import com.gucardev.springreactboilerplate.features.core.file.domain.model.ValidatedUpload;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Image upload: validates the upload, confirms it is really an image (magic bytes), re-encodes it to
 * an optimized WebP, generates a WebP thumbnail (stored under the same id with a {@code _tb} suffix),
 * and persists both. Use {@link UploadFileService} for non-image files (stored as-is).
 */
@Service
@RequiredArgsConstructor
public class UploadImageService implements UploadImageUseCase {

    private static final String THUMBNAIL_SUFFIX = "_tb";

    private final FileValidationPort validationPort;
    private final ImageProcessingPort imageProcessingPort;
    private final FileStoragePort fileStoragePort;
    private final SaveFilePort saveFilePort;

    @Override
    @Transactional
    public StoredFile uploadImage(UploadFileCommand command) {
        ValidatedUpload upload = validationPort.validate(command.originalFilename(), command.content());
        if (!upload.contentType().startsWith("image/")) {
            throw FileExceptionType.NOT_AN_IMAGE.toException();
        }

        OptimizedImage optimized = imageProcessingPort.optimize(upload.content());

        UUID id = UUID.randomUUID();
        String mainKey = id + ImageProcessingPort.WEBP_EXTENSION;
        String thumbnailKey = id + THUMBNAIL_SUFFIX + ImageProcessingPort.WEBP_EXTENSION;
        StorageType target = fileStoragePort.resolveUploadTarget(command.storageType());

        StoredFile stored = saveFilePort.save(StoredFile.builder()
                .id(id)
                .originalFilename(FilenameSanitizer.stripExtension(upload.sanitizedFilename()) + ImageProcessingPort.WEBP_EXTENSION)
                .storageKey(mainKey)
                .thumbnailKey(thumbnailKey)
                .storageType(target)
                .contentType(ImageProcessingPort.WEBP_CONTENT_TYPE)
                .size((long) optimized.main().length)
                .extension(ImageProcessingPort.WEBP_EXTENSION)
                .build());

        fileStoragePort.store(target, mainKey, optimized.main(), ImageProcessingPort.WEBP_CONTENT_TYPE);
        fileStoragePort.store(target, thumbnailKey, optimized.thumbnail(), ImageProcessingPort.WEBP_CONTENT_TYPE);

        return stored;
    }
}
