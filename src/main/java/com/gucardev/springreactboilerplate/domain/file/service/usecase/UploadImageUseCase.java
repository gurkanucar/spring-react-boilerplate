package com.gucardev.springreactboilerplate.domain.file.service.usecase;

import com.gucardev.springreactboilerplate.domain.file.entity.StoredFile;
import com.gucardev.springreactboilerplate.domain.file.enums.StorageType;
import com.gucardev.springreactboilerplate.domain.file.exception.FileExceptionType;
import com.gucardev.springreactboilerplate.domain.file.mapper.FileMapper;
import com.gucardev.springreactboilerplate.domain.file.model.dto.FileResponseDto;
import com.gucardev.springreactboilerplate.domain.file.repository.FileRepository;
import com.gucardev.springreactboilerplate.domain.file.service.FileValidator;
import com.gucardev.springreactboilerplate.domain.file.service.FilenameSanitizer;
import com.gucardev.springreactboilerplate.domain.file.service.ImageOptimizer;
import com.gucardev.springreactboilerplate.domain.file.service.OptimizedImage;
import com.gucardev.springreactboilerplate.domain.file.service.ValidatedUpload;
import com.gucardev.springreactboilerplate.domain.file.storage.StorageProvider;
import com.gucardev.springreactboilerplate.domain.file.storage.StorageProviderRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Image upload: validates the upload, confirms it is really an image (magic bytes), re-encodes it to
 * an optimized WebP, generates a WebP thumbnail (stored under the same id with a {@code _tb} suffix),
 * and persists both. Use {@link UploadFileUseCase} for non-image files (stored as-is).
 */
@Service
@RequiredArgsConstructor
public class UploadImageUseCase {

    private static final String THUMBNAIL_SUFFIX = "_tb";

    private final FileValidator validator;
    private final ImageOptimizer imageOptimizer;
    private final StorageProviderRegistry storageRegistry;
    private final FileRepository repository;
    private final FileMapper mapper;

    /**
     * @param storageType backend to store to; {@code null} uses the configured default.
     */
    @Transactional
    public FileResponseDto execute(MultipartFile file, StorageType storageType) {
        ValidatedUpload upload = validator.validate(file);
        if (!upload.contentType().startsWith("image/")) {
            throw FileExceptionType.NOT_AN_IMAGE.toException();
        }

        OptimizedImage optimized = imageOptimizer.optimize(upload.content());

        UUID id = UUID.randomUUID();
        String mainKey = id + ImageOptimizer.WEBP_EXTENSION;
        String thumbnailKey = id + THUMBNAIL_SUFFIX + ImageOptimizer.WEBP_EXTENSION;
        StorageProvider provider = storageRegistry.resolveForUpload(storageType);

        StoredFile stored = StoredFile.builder()
                .id(id)
                .originalFilename(FilenameSanitizer.stripExtension(upload.sanitizedFilename()) + ImageOptimizer.WEBP_EXTENSION)
                .storageKey(mainKey)
                .thumbnailKey(thumbnailKey)
                .storageType(provider.type())
                .contentType(ImageOptimizer.WEBP_CONTENT_TYPE)
                .size((long) optimized.main().length)
                .extension(ImageOptimizer.WEBP_EXTENSION)
                .build();
        repository.save(stored);

        provider.store(mainKey, optimized.main(), ImageOptimizer.WEBP_CONTENT_TYPE);
        provider.store(thumbnailKey, optimized.thumbnail(), ImageOptimizer.WEBP_CONTENT_TYPE);

        return mapper.toDto(stored);
    }
}
